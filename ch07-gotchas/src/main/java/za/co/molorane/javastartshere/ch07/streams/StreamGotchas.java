package za.co.molorane.javastartshere.ch07.streams;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Demonstrates Stream pipeline traps, laziness, and stream reuse.
 * <p>
 * Key Concepts (Chapter 7 Topic 5):
 * <ul>
 *   <li><b>Stream Single-Use Rule:</b> Streams cannot be reused after a terminal operation has been executed.
 *       Reusing a stream throws {@link IllegalStateException}.</li>
 *   <li><b>Laziness:</b> Intermediate operations never execute without a terminal operation.</li>
 * </ul>
 */
public class StreamGotchas {

    /**
     * Demonstrates that intermediate stream operations do not execute without a terminal operation.
     */
    public static int countWithoutTerminalOperation(Stream<String> stream, AtomicInteger counter) {
        stream.map(s -> {
            counter.incrementAndGet();
            return s.toUpperCase();
        });
        return counter.get(); // Returns 0 because no terminal operation was invoked!
    }

    /**
     * Demonstrates that attempting to operate on an already-consumed stream throws IllegalStateException.
     */
    public static void reuseConsumedStream(Stream<String> stream) {
        stream.count(); // Terminal operation consumes stream
        stream.findFirst(); // Throws IllegalStateException!
    }
}
