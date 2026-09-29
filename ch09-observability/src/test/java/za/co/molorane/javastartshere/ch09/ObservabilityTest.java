package za.co.molorane.javastartshere.ch09;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Chapter 9: Java Observability, Tracing & Metrics Tests")
class ObservabilityTest {

    @Test
    @DisplayName("Should inject and cleanly remove MDC trace ID context")
    void shouldPropagateAndCleanMdcTrace() throws Exception {
        String testTrace = "trace-xyz-9876";

        String result = MdcContextPropagator.runWithTrace(testTrace, () -> {
            assertThat(MDC.get(MdcContextPropagator.TRACE_ID_KEY)).isEqualTo(testTrace);
            return "SUCCESS";
        });

        assertThat(result).isEqualTo("SUCCESS");
        // Verify MDC was cleaned up to prevent thread pool contamination
        assertThat(MDC.get(MdcContextPropagator.TRACE_ID_KEY)).isNull();
    }

    @Test
    @DisplayName("Should propagate MDC context across thread pool boundaries and clean up worker threads")
    void shouldPropagateMdcAcrossThreadPoolBoundaries() throws Exception {
        ExecutorService singleWorkerPool = Executors.newSingleThreadExecutor();
        MdcContextPropagator.ContextPropagatingExecutor propagatingExecutor =
                new MdcContextPropagator.ContextPropagatingExecutor(singleWorkerPool);

        AtomicReference<String> workerCapturedCorrelationId = new AtomicReference<>();
        AtomicReference<String> workerCapturedTraceId = new AtomicReference<>();

        try {
            // Set context on the caller thread
            MDC.put(MdcContextPropagator.CORRELATION_ID_KEY, "corr-abc-123");
            MDC.put(MdcContextPropagator.TRACE_ID_KEY, "trace-xyz-789");

            CompletableFuture<Void> future = new CompletableFuture<>();
            propagatingExecutor.execute(() -> {
                try {
                    workerCapturedCorrelationId.set(MDC.get(MdcContextPropagator.CORRELATION_ID_KEY));
                    workerCapturedTraceId.set(MDC.get(MdcContextPropagator.TRACE_ID_KEY));
                    future.complete(null);
                } catch (Throwable t) {
                    future.completeExceptionally(t);
                }
            });

            future.get(2, TimeUnit.SECONDS);

            // Assert that the worker thread received the parent's MDC context
            assertThat(workerCapturedCorrelationId.get()).isEqualTo("corr-abc-123");
            assertThat(workerCapturedTraceId.get()).isEqualTo("trace-xyz-789");

            // Now run a raw (unwrapped) task on the same pooled worker thread to ensure it was cleaned!
            CompletableFuture<String> leakageCheckFuture = new CompletableFuture<>();
            singleWorkerPool.execute(() -> {
                leakageCheckFuture.complete(MDC.get(MdcContextPropagator.CORRELATION_ID_KEY));
            });

            String residualMdc = leakageCheckFuture.get(2, TimeUnit.SECONDS);
            // Must be null! Context must never leak into reused worker threads.
            assertThat(residualMdc).isNull();

        } finally {
            MDC.clear();
            singleWorkerPool.shutdownNow();
        }
    }

    @Test
    @DisplayName("Should mask sensitive PII (credit cards, national IDs, bearer tokens)")
    void shouldMaskSensitivePiiData() {
        String logWithCreditCard = "Payment failed for card 4111222233334444: insufficient funds";
        String maskedCard = SensitiveDataMasker.mask(logWithCreditCard);
        assertThat(maskedCard).isEqualTo("Payment failed for card ****-****-****-****: insufficient funds");

        String logWithHyphenatedCard = "Card 5500-1234-5678-9012 charged 450.00 ZAR";
        assertThat(SensitiveDataMasker.mask(logWithHyphenatedCard))
                .isEqualTo("Card ****-****-****-**** charged 450.00 ZAR");

        String logWithSaId = "Customer with SA ID 9201015009087 registered successfully";
        String maskedId = SensitiveDataMasker.mask(logWithSaId);
        assertThat(maskedId).isEqualTo("Customer with SA ID ************* registered successfully");

        String logWithBearer = "Outbound request with Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        String maskedBearer = SensitiveDataMasker.mask(logWithBearer);
        assertThat(maskedBearer).contains("Authorization: Bearer [REDACTED_TOKEN]");

        // Non-sensitive data remains uncorrupted
        String cleanLog = "Order 98412 processed successfully for amount 150.00";
        assertThat(SensitiveDataMasker.mask(cleanLog)).isEqualTo(cleanLog);
    }

    @Test
    @DisplayName("Should parse, validate, and generate W3C TraceContext headers")
    void shouldManageW3cTraceContext() {
        String rawHeader = "00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01";
        W3cTraceContext context = W3cTraceContext.parse(rawHeader);

        assertThat(context.version()).isEqualTo("00");
        assertThat(context.traceId()).isEqualTo("4bf92f3577b34da6a3ce929d0e0e4736");
        assertThat(context.spanId()).isEqualTo("00f067aa0ba902b7");
        assertThat(context.flags()).isEqualTo("01");
        assertThat(context.isSampled()).isTrue();
        assertThat(context.toHeaderValue()).isEqualTo(rawHeader);

        // Create child span: preserves traceId and flags, updates spanId
        W3cTraceContext childSpan = context.createChildSpan();
        assertThat(childSpan.traceId()).isEqualTo(context.traceId());
        assertThat(childSpan.flags()).isEqualTo(context.flags());
        assertThat(childSpan.spanId()).isNotEqualTo(context.spanId()).hasSize(16);

        // Root generation
        W3cTraceContext root = W3cTraceContext.createRoot();
        assertThat(root.version()).isEqualTo("00");
        assertThat(root.traceId()).hasSize(32);
        assertThat(root.spanId()).hasSize(16);
        assertThat(root.isSampled()).isTrue();

        // Validation failures
        assertThatThrownBy(() -> W3cTraceContext.parse("99-1234-5678-01"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> W3cTraceContext.parse("00-00000000000000000000000000000000-00f067aa0ba902b7-01"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid W3C traceId");
    }

    @Test
    @DisplayName("Should instrument four core Micrometer meter primitives accurately")
    void shouldRecordFourMeterPrimitives() throws Exception {
        MeterRegistry registry = new SimpleMeterRegistry();
        ServiceMetricsEngine metrics = new ServiceMetricsEngine(registry);

        // 1. Counter
        metrics.recordPayment("stripe", true);
        metrics.recordPayment("stripe", true);
        metrics.recordPayment("stripe", false);
        metrics.recordPayment("paypal", true);

        Counter stripeSuccess = registry.find("payment.transactions")
                .tag("gateway", "stripe")
                .tag("status", "success")
                .counter();
        assertThat(stripeSuccess).isNotNull();
        assertThat(stripeSuccess.count()).isEqualTo(2.0);

        Counter stripeFailure = registry.find("payment.transactions")
                .tag("gateway", "stripe")
                .tag("status", "failure")
                .counter();
        assertThat(stripeFailure).isNotNull();
        assertThat(stripeFailure.count()).isEqualTo(1.0);

        // 2. Gauge
        ArrayDeque<String> queue = new ArrayDeque<>(List.of("task1", "task2", "task3"));
        metrics.registerQueueGauge("order-dispatch", queue);

        double initialGauge = registry.get("task.queue.depth").tag("queue", "order-dispatch").gauge().value();
        assertThat(initialGauge).isEqualTo(3.0);

        queue.add("task4");
        double updatedGauge = registry.get("task.queue.depth").tag("queue", "order-dispatch").gauge().value();
        assertThat(updatedGauge).isEqualTo(4.0);

        // 3. Timer
        String result = metrics.recordOrderProcessing(() -> {
            Thread.sleep(10);
            return "PROCESSED";
        });
        assertThat(result).isEqualTo("PROCESSED");

        Timer timer = registry.find("order.processing.duration").timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1L);
        assertThat(timer.totalTime(TimeUnit.MILLISECONDS)).isGreaterThanOrEqualTo(10.0);

        // 4. DistributionSummary
        metrics.recordPayloadSize(1024);
        metrics.recordPayloadSize(4096);

        double totalBytes = registry.get("payload.size.bytes").summary().totalAmount();
        assertThat(totalBytes).isEqualTo(5120.0);
    }

    @Test
    @DisplayName("Should reject high-cardinality tags to prevent TSDB memory collapse")
    void shouldRejectHighCardinalityTags() {
        // Safe bounded tags pass
        ServiceMetricsEngine.validateTagValue("region", "eu-west-1");
        ServiceMetricsEngine.validateTagValue("environment", "prod");

        // High cardinality UUID fails
        assertThatThrownBy(() -> ServiceMetricsEngine.validateTagValue("userId", "c8f2a1b9-91e2-4f81-8e9a-7a5b3c2d1e0f"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cardinality Explosion Violation");

        // High cardinality email fails
        assertThatThrownBy(() -> ServiceMetricsEngine.validateTagValue("email", "customer@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cardinality Explosion Violation");
    }

    @Test
    @DisplayName("Should report no deadlocks on a healthy JVM")
    void shouldReportNoDeadlocksWhenHealthy() {
        ThreadDeadlockDetector detector = new ThreadDeadlockDetector();
        List<ThreadDeadlockDetector.DeadlockReport> deadlocks = detector.detectDeadlocks();
        assertThat(deadlocks).isEmpty();

        String diagnosticText = detector.generateDeadlockDiagnosticText();
        assertThat(diagnosticText).contains("No Java-level deadlocks detected");
    }
}
