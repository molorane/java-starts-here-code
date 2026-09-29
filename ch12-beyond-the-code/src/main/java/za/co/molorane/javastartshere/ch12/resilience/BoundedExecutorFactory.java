package za.co.molorane.javastartshere.ch12.resilience;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Defensive ThreadPool Factory countering the "Vibe Coding" Unbounded Queue Trap.
 *
 * Never uses {@code Executors.newFixedThreadPool()} in production because it allocates
 * an unbounded {@code LinkedBlockingQueue} of {@code Integer.MAX_VALUE} capacity,
 * creating a memory bomb that crashes the JVM with OOM under load spikes.
 *
 * Enforces strictly bounded queues with explicit backpressure saturation policies.
 */
public final class BoundedExecutorFactory {

    private BoundedExecutorFactory() {}

    public static ThreadPoolExecutor createResilientPool(
            int corePoolSize,
            int maxPoolSize,
            int queueCapacity,
            RejectedExecutionHandler saturationPolicy
    ) {
        if (queueCapacity <= 0 || queueCapacity == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Queue capacity must be strictly bounded and positive: " + queueCapacity);
        }

        BlockingQueue<Runnable> boundedQueue = new ArrayBlockingQueue<>(queueCapacity);

        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L,
                TimeUnit.SECONDS,
                boundedQueue,
                saturationPolicy
        );
    }

    /**
     * Creates a pool with {@link ThreadPoolExecutor.CallerRunsPolicy}, which throttles upstream
     * producers and preserves zero dropped tasks during traffic spikes.
     */
    public static ThreadPoolExecutor createThrottlingPool(int poolSize, int queueCapacity) {
        return createResilientPool(poolSize, poolSize, queueCapacity, new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
