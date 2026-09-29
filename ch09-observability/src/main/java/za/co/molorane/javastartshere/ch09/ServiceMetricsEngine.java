package za.co.molorane.javastartshere.ch09;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

/**
 * Demonstrates the 4 fundamental meter types in Micrometer:
 * 1. {@link Counter}: monotonically increasing values (payments, requests).
 * 2. {@link Gauge}: instantaneous fluctuating state (queue depths, pool levels).
 * 3. {@link Timer}: latency distribution (percentiles p50, p95, p99) and event throughput.
 * 4. {@link DistributionSummary}: non-time distributions (payload sizes in bytes).
 *
 * Enforces the "Golden Cardinality Rule": metric tags must belong to a small, bounded set.
 * High-cardinality values (UUIDs, emails, order IDs) must never be added as metric tags.
 */
public class ServiceMetricsEngine {

    private static final Set<String> ALLOWED_GATEWAYS = Set.of("stripe", "paypal", "ozow", "peach");
    private static final Pattern UUID_OR_NUMERIC_PATTERN =
            Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$|^\\d+$", Pattern.CASE_INSENSITIVE);

    private final MeterRegistry registry;
    private final Timer orderProcessingTimer;
    private final DistributionSummary payloadSizeSummary;

    public ServiceMetricsEngine(MeterRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry cannot be null");

        this.orderProcessingTimer = Timer.builder("order.processing.duration")
                .description("Time taken to process and validate an order")
                .publishPercentiles(0.50, 0.95, 0.99)
                .publishPercentileHistogram()
                .register(registry);

        this.payloadSizeSummary = DistributionSummary.builder("payload.size.bytes")
                .description("Distribution of incoming request payload sizes in bytes")
                .baseUnit("bytes")
                .register(registry);
    }

    /**
     * Records a payment transaction with bounded tags (gateway, status).
     * Rejects high-cardinality tags.
     */
    public void recordPayment(String gateway, boolean success) {
        validateBoundedTag("gateway", gateway, ALLOWED_GATEWAYS);
        Counter.builder("payment.transactions")
                .tag("gateway", gateway.toLowerCase())
                .tag("status", success ? "success" : "failure")
                .description("Total number of processed payment transactions")
                .register(registry)
                .increment();
    }

    /**
     * Registers a Gauge to track instantaneous queue depth dynamically on scrape.
     */
    public <T extends Collection<?>> void registerQueueGauge(String queueName, T queue) {
        validateTagKey(queueName);
        Gauge.builder("task.queue.depth", queue, Collection::size)
                .tag("queue", queueName)
                .description("Instantaneous number of pending tasks in queue")
                .register(registry);
    }

    /**
     * Times an execution block, capturing p50, p95, and p99 percentiles.
     */
    public <T> T recordOrderProcessing(Callable<T> operation) throws Exception {
        return orderProcessingTimer.recordCallable(operation);
    }

    public void recordOrderProcessing(Runnable operation) {
        orderProcessingTimer.record(operation);
    }

    /**
     * Records the size of a payload in bytes into a distribution summary.
     */
    public void recordPayloadSize(long bytes) {
        payloadSizeSummary.record(bytes);
    }

    /**
     * The Golden Cardinality Rule:
     * Guards against high-cardinality tag explosions in TSDB (Prometheus/Datadog).
     */
    public static void validateTagValue(String tagKey, String tagValue) {
        if (tagValue == null || tagValue.isBlank()) {
            throw new IllegalArgumentException("Tag value for '" + tagKey + "' cannot be blank");
        }
        if (UUID_OR_NUMERIC_PATTERN.matcher(tagValue).matches() || tagValue.contains("@")) {
            throw new IllegalArgumentException(
                    "Cardinality Explosion Violation! High-cardinality tag value detected for key '"
                            + tagKey + "': " + tagValue + ". High-cardinality values belong in logs/traces, NOT metric tags."
            );
        }
    }

    private static void validateBoundedTag(String tagKey, String tagValue, Set<String> allowedSet) {
        if (tagValue == null || !allowedSet.contains(tagValue.toLowerCase())) {
            throw new IllegalArgumentException("Invalid or unbounded tag value for " + tagKey + ": " + tagValue);
        }
    }

    private static void validateTagKey(String tagKey) {
        if (tagKey == null || tagKey.isBlank()) {
            throw new IllegalArgumentException("Tag key must not be blank");
        }
    }

    public MeterRegistry getRegistry() {
        return registry;
    }
}
