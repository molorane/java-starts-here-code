package za.co.molorane.javastartshere.ch11;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstrates why the volatile keyword is NOT sufficient for thread safety on compound operations:
 * - {@code volatile} guarantees Visibility and Ordering (Happens-Before memory barrier).
 * - {@code volatile} does NOT guarantee Atomicity: {@code count++} is 3 bytecode operations (iload, iadd, istore).
 * - {@link AtomicInteger} guarantees Atomicity via hardware CAS (Compare-And-Swap).
 */
public class VolatileAtomicityGotcha {

    private volatile int volatileCount = 0;
    private final AtomicInteger atomicCount = new AtomicInteger(0);

    /**
     * Non-atomic: susceptible to lost updates under concurrent execution.
     */
    public void incrementVolatile() {
        volatileCount++;
    }

    /**
     * Atomic: thread-safe lock-free increment.
     */
    public void incrementAtomic() {
        atomicCount.incrementAndGet();
    }

    public int getVolatileCount() {
        return volatileCount;
    }

    public int getAtomicCount() {
        return atomicCount.get();
    }
}
