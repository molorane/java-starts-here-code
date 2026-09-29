package za.co.molorane.javastartshere.ch10;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Chapter 10: Enterprise Cloud Toolkit & Resilience Tests")
class EnterpriseToolkitTest {

    @Test
    @DisplayName("Should drain in-flight requests and reject new traffic during graceful shutdown")
    void shouldDrainInFlightRequestsGracefully() throws Exception {
        GracefulShutdownCoordinator coordinator = new GracefulShutdownCoordinator();
        ExecutorService executor = Executors.newFixedThreadPool(4);

        CountDownLatch taskStarted = new CountDownLatch(1);
        CountDownLatch shutdownStarted = new CountDownLatch(1);
        AtomicBoolean taskCompletedCleanly = new AtomicBoolean(false);

        // 1. Submit an in-flight transaction
        CompletableFuture<String> inFlightTx = CompletableFuture.supplyAsync(() -> {
            try {
                return coordinator.execute(() -> {
                    taskStarted.countDown();
                    // Wait for shutdown trigger
                    shutdownStarted.await(2, TimeUnit.SECONDS);
                    Thread.sleep(50);
                    taskCompletedCleanly.set(true);
                    return "TRANSACTION_COMMITTED";
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executor);

        taskStarted.await(2, TimeUnit.SECONDS);
        assertThat(coordinator.getInFlightCount()).isEqualTo(1);

        // 2. Trigger graceful shutdown in the background
        CompletableFuture<Boolean> shutdownFuture = CompletableFuture.supplyAsync(() -> {
            try {
                shutdownStarted.countDown();
                return coordinator.commenceShutdown(Duration.ofSeconds(2));
            } catch (InterruptedException e) {
                return false;
            }
        }, executor);

        // Wait for state to transition to DRAINING
        shutdownStarted.await(2, TimeUnit.SECONDS);
        Thread.sleep(10);
        assertThat(coordinator.getState()).isEqualTo(GracefulShutdownCoordinator.LifecycleState.DRAINING);

        // 3. New requests submitted during DRAINING must be rejected immediately (HTTP 503)
        assertThatThrownBy(() -> coordinator.execute(() -> "NEW_REQUEST"))
                .isInstanceOf(GracefulShutdownCoordinator.ServiceUnavailableException.class)
                .hasMessageContaining("Server is shutting down");

        // 4. Verify in-flight transaction completes successfully
        String txResult = inFlightTx.get(2, TimeUnit.SECONDS);
        assertThat(txResult).isEqualTo("TRANSACTION_COMMITTED");
        assertThat(taskCompletedCleanly.get()).isTrue();

        // 5. Verify shutdown terminates cleanly
        boolean drained = shutdownFuture.get(2, TimeUnit.SECONDS);
        assertThat(drained).isTrue();
        assertThat(coordinator.getState()).isEqualTo(GracefulShutdownCoordinator.LifecycleState.STOPPED);
        assertThat(coordinator.getInFlightCount()).isEqualTo(0);

        executor.shutdownNow();
    }

    @Test
    @DisplayName("Should decouple Liveness from downstream dependencies and track Readiness independently")
    void shouldDecoupleLivenessFromReadiness() {
        EnterpriseHealthIndicator.InternalLivenessIndicator liveness =
                new EnterpriseHealthIndicator.InternalLivenessIndicator();
        EnterpriseHealthIndicator.TrafficReadinessIndicator readiness =
                new EnterpriseHealthIndicator.TrafficReadinessIndicator();

        // 1. Initial State: Liveness UP, Readiness DOWN (starting/warming up)
        assertThat(liveness.health().getStatus()).isEqualTo(Status.UP);
        assertThat(readiness.health().getStatus()).isEqualTo(Status.DOWN);

        // 2. Warming completes, database connects: Readiness UP
        readiness.setAcceptingTraffic(true);
        readiness.setDatabaseConnected(true);
        assertThat(readiness.health().getStatus()).isEqualTo(Status.UP);

        // 3. Database connection drops: Readiness goes DOWN, but Liveness stays UP!
        // (Preventing cascading Kubernetes pod restart storms!)
        readiness.setDatabaseConnected(false);
        assertThat(readiness.health().getStatus()).isEqualTo(Status.DOWN);
        assertThat(liveness.health().getStatus()).isEqualTo(Status.UP);

        // 4. Internal JVM failure: Liveness goes DOWN, prompting Kubernetes to restart pod
        liveness.markBroken();
        assertThat(liveness.health().getStatus()).isEqualTo(Status.DOWN);
    }

    @Test
    @DisplayName("Should compute container-aware heap allocation and protect off-heap headroom")
    void shouldComputeContainerMemoryErgonomics() {
        // Standard production 2GB container limit with 75% MaxRAMPercentage
        ContainerErgonomicsCalculator calc = new ContainerErgonomicsCalculator(2048, 75.0);

        assertThat(calc.calculateMaxHeapMemoryMb()).isEqualTo(1536);
        assertThat(calc.calculateOffHeapHeadroomMb()).isEqualTo(512);
        assertThat(calc.isOffHeapHeadroomSufficient(300)).isTrue();

        // Guard against dangerous > 80% settings that cause kernel OOM killer (Exit 137)
        assertThatThrownBy(() -> new ContainerErgonomicsCalculator(2048, 95.0))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Unsafe MaxRAMPercentage");
    }

    @Test
    @DisplayName("Should verify enterprise production Kubernetes deployment and Dockerfile standards")
    void shouldVerifyProductionManifestStandards() throws Exception {
        File dockerfile = new File("Dockerfile");
        File k8s = new File("k8s/deployment.yaml");

        assertThat(dockerfile).exists();
        assertThat(k8s).exists();

        String dockerContent = Files.readString(dockerfile.toPath());
        assertThat(dockerContent).contains("appuser");
        assertThat(dockerContent).contains("-XX:MaxRAMPercentage=75.0");
        assertThat(dockerContent).contains("layertools");

        String k8sContent = Files.readString(k8s.toPath());
        assertThat(k8sContent).contains("terminationGracePeriodSeconds: 30");
        assertThat(k8sContent).contains("/actuator/health/liveness");
        assertThat(k8sContent).contains("/actuator/health/readiness");
        assertThat(k8sContent).contains("resources:");
    }
}
