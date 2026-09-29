package za.co.molorane.javastartshere.ch09;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Chapter 9: Java Observability & Metrics Tests")
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
        // Verify MDC was cleaned up to prevent thread pool contamination!
        assertThat(MDC.get(MdcContextPropagator.TRACE_ID_KEY)).isNull();
    }

    @Test
    @DisplayName("Should increment Micrometer production metrics accurately")
    void shouldRecordMicrometerMetrics() {
        MeterRegistry registry = new SimpleMeterRegistry();
        Counter ordersCounter = Counter.builder("orders.placed")
                .tag("region", "af-south-1")
                .description("Total number of processed orders")
                .register(registry);

        ordersCounter.increment();
        ordersCounter.increment();

        assertThat(ordersCounter.count()).isEqualTo(2.0);
    }
}
