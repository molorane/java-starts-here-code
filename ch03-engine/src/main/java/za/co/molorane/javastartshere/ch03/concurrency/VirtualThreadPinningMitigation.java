package za.co.molorane.javastartshere.ch03.concurrency;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Demonstrates the Virtual Thread "Carrier Pinning" trap and its production mitigation.
 * <p>
 * Key Concepts:
 * <ul>
 *   <li><b>Carrier Pinning Trap:</b> When a virtual thread executes inside a {@code synchronized} block
 *       or native method and attempts a blocking operation (e.g., I/O or sleep), the JVM cannot unmount
 *       the virtual thread. The underlying OS platform carrier thread becomes "pinned" and blocked.
 *       If enough virtual threads pin their carriers, the entire carrier pool is exhausted, starving
 *       all other tasks.</li>
 *   <li><b>Mitigation with ReentrantLock:</b> {@link ReentrantLock} relies on {@code LockSupport.park()},
 *       allowing the JVM Continuation engine to cleanly unmount the virtual thread from its carrier
 *       during contention or blocking operations.</li>
 * </ul>
 */
public class VirtualThreadPinningMitigation {

    private final ReentrantLock lock = new ReentrantLock();
    private final Object monitor = new Object();
    private final AtomicInteger processedWithLock = new AtomicInteger(0);
    private final AtomicInteger processedWithSync = new AtomicInteger(0);

    /**
     * Non-pinning safe pattern: Uses ReentrantLock.
     * The virtual thread unmounts from its carrier thread during contention or sleep.
     */
    public void executeSafeTask(Duration delay) {
        lock.lock();
        try {
            if (!delay.isZero()) {
                Thread.sleep(delay);
            }
            processedWithLock.incrementAndGet();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Pinning-prone pattern: Uses synchronized monitor lock.
     * When blocked on I/O or sleep, this pins the underlying OS carrier thread.
     */
    public void executePinningTask(Duration delay) {
        synchronized (monitor) {
            try {
                if (!delay.isZero()) {
                    Thread.sleep(delay);
                }
                processedWithSync.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public int getProcessedWithLock() {
        return processedWithLock.get();
    }

    public int getProcessedWithSync() {
        return processedWithSync.get();
    }

    /**
     * Runs N virtual threads concurrently using the non-pinning ReentrantLock approach.
     */
    public static int runConcurrentLockTasks(int count) throws InterruptedException {
        VirtualThreadPinningMitigation coordinator = new VirtualThreadPinningMitigation();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < count; i++) {
                executor.submit(() -> coordinator.executeSafeTask(Duration.ofMillis(1)));
            }
        }
        return coordinator.getProcessedWithLock();
    }
}
