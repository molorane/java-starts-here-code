package za.co.molorane.javastartshere.ch03;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstrates Project Loom virtual threads introduced in modern Java:
 * - 1MB platform thread tax eliminated.
 * - Thousands of concurrent virtual tasks mount and unmount over carrier threads.
 */
public class VirtualThreadEngine {

    public static int executeConcurrentTasks(int taskCount) throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < taskCount; i++) {
                executor.submit(() -> {
                    counter.incrementAndGet();
                    return counter.get();
                });
            }
        } // ExecutorService implements AutoCloseable; awaits completion
        return counter.get();
    }
}
