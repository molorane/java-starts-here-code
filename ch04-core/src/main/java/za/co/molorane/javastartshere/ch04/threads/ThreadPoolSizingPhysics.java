package za.co.molorane.javastartshere.ch04.threads;

import java.util.concurrent.*;

/**
 * Demonstrates Thread Pool Sizing Physics and the Unbounded Queue Trap.
 * <p>
 * Key Concepts (Chapter 4 Topic 7):
 * <ul>
 *   <li><b>Brian Goetz Formula:</b>
 *       {@code N_threads = N_cpu * U_cpu * (1 + W/C)}
 *       where W/C is the wait-time-to-compute-time ratio.</li>
 *   <li><b>The Unbounded Queue Trap:</b> {@link Executors#newFixedThreadPool(int)} uses an unbounded
 *       {@link LinkedBlockingQueue} (capacity 2^31 - 1). Under load spikes, requests queue indefinitely,
 *       starving heap memory and precipitating OutOfMemoryErrors.</li>
 *   <li><b>Production Best Practice:</b> Always use bounded queues with explicit rejection policies
 *       (e.g., {@link ThreadPoolExecutor.CallerRunsPolicy}).</li>
 * </ul>
 */
public class ThreadPoolSizingPhysics {

    /**
     * Calculates the optimal pool size based on Brian Goetz's formula.
     *
     * @param cpuCores available CPU cores
     * @param targetCpuUtilization desired CPU usage (e.g. 0.8 for 80%)
     * @param waitTimeToComputeRatio ratio of I/O wait time to CPU processing time (e.g. 4.0 if 80ms wait / 20ms compute)
     * @return recommended worker thread count
     */
    public static int calculateOptimalPoolSize(int cpuCores, double targetCpuUtilization, double waitTimeToComputeRatio) {
        if (cpuCores <= 0 || targetCpuUtilization <= 0 || waitTimeToComputeRatio < 0) {
            throw new IllegalArgumentException("Invalid pool sizing parameters");
        }
        return (int) Math.ceil(cpuCores * targetCpuUtilization * (1.0 + waitTimeToComputeRatio));
    }

    /**
     * Builds a production-grade executor with a bounded queue and backpressure rejection policy.
     */
    public static ThreadPoolExecutor createResilientPool(int coreThreads, int maxThreads, int queueCapacity) {
        return new ThreadPoolExecutor(
                coreThreads,
                maxThreads,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueCapacity),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy() // Backpressure: caller thread executes task if saturated
        );
    }
}
