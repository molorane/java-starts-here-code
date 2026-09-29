package za.co.molorane.javastartshere.ch04;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.molorane.javastartshere.ch04.threads.ThreadPoolSizingPhysics;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Chapter 4: Thread Pool Sizing Physics & Resilient Executors Tests")
class ThreadPoolPhysicsTest {

    @Test
    @DisplayName("Brian Goetz sizing formula: Compute-bound task vs I/O-bound enterprise service")
    void shouldCalculateOptimalPoolSizes() {
        int cpuCores = 8;
        double targetCpuUtilization = 0.8; // 80%

        // Scenario A: Compute-bound task (Wait/Compute ratio ~ 0.1)
        // N_threads = 8 * 0.8 * (1 + 0.1) = 7.04 -> ceil = 8 threads
        int computePoolSize = ThreadPoolSizingPhysics.calculateOptimalPoolSize(cpuCores, targetCpuUtilization, 0.1);
        assertThat(computePoolSize).isEqualTo(8);

        // Scenario B: I/O-bound service (e.g., 80ms DB wait / 20ms CPU processing -> W/C = 4.0)
        // N_threads = 8 * 0.8 * (1 + 4.0) = 32 threads
        int ioPoolSize = ThreadPoolSizingPhysics.calculateOptimalPoolSize(cpuCores, targetCpuUtilization, 4.0);
        assertThat(ioPoolSize).isEqualTo(32);
    }

    @Test
    @DisplayName("Bounded executor applies CallerRunsPolicy backpressure when saturated")
    void shouldApplyBackpressureUnderSaturation() throws InterruptedException {
        int coreThreads = 2;
        int maxThreads = 2;
        int queueCapacity = 2;
        ThreadPoolExecutor executor = ThreadPoolSizingPhysics.createResilientPool(coreThreads, maxThreads, queueCapacity);

        AtomicInteger processedTasks = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(10);

        try {
            for (int i = 0; i < 10; i++) {
                executor.submit(() -> {
                    try {
                        Thread.sleep(10);
                        processedTasks.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            boolean completed = latch.await(5, TimeUnit.SECONDS);
            assertThat(completed).isTrue();
            assertThat(processedTasks.get()).isEqualTo(10);
        } finally {
            executor.shutdown();
        }
    }
}
