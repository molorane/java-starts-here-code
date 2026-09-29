package za.co.molorane.javastartshere.ch11;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * Common concurrency interview questions and idioms:
 * 1. {@link AtomicLong} vs {@link LongAdder}:
 *    - AtomicLong uses CAS (Compare-And-Swap) spinning; under high contention, cache line bouncing degrades throughput.
 *    - LongAdder uses a striped array of Cells to distribute contention, aggregating sums lazily.
 * 2. Volatile happens-before visibility vs mutual exclusion.
 */
public class ConcurrencyInterviewBasics {

    private final AtomicLong atomicCounter = new AtomicLong(0);
    private final LongAdder longAdder = new LongAdder();

    public void incrementAtomic() {
        atomicCounter.incrementAndGet();
    }

    public void incrementAdder() {
        longAdder.increment();
    }

    public long getAtomicValue() {
        return atomicCounter.get();
    }

    public long getAdderValue() {
        return longAdder.sum();
    }
}
