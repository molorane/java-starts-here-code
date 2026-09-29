package za.co.molorane.javastartshere.ch07;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Chapter 7: Java Gotchas & Puzzles Tests")
class GotchasPuzzlesTest {

    @Test
    @DisplayName("Gotcha: Integer Cache (-128 to 127) identity vs equality trap")
    void testIntegerCacheTrap() {
        Integer a = 100;
        Integer b = 100;
        // Within byte cache range [-128, 127], references are identical
        assertThat(a == b).isTrue();

        Integer c = 200;
        Integer d = 200;
        // Outside byte cache range, new objects are allocated on the heap!
        assertThat(c == d).isFalse();
        assertThat(c.equals(d)).isTrue();
    }

    @Test
    @DisplayName("Puzzle 1: The Phantom Subtraction Trap (Primitive Collection vs Object)")
    void testPhantomSubtractionTrap() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);

        // list.remove(1) interprets '1' as primitive int index, removing element at index 1 ('2')
        list.remove(1);
        assertThat(list).containsExactly(1, 3);

        // To remove by value, explicit boxing or Integer.valueOf is required!
        list.remove(Integer.valueOf(1));
        assertThat(list).containsExactly(3);
    }

    @Test
    @DisplayName("Puzzle 2: The Ghost in the Pipeline (Spliterator.SIZED optimization skips peek)")
    void testGhostInThePipeline() {
        AtomicInteger peekCount = new AtomicInteger(0);
        List<String> items = List.of("alpha", "beta", "gamma");

        // count() on a SIZED collection optimizes away intermediate operations like peek()!
        long count = items.stream()
                .peek(item -> peekCount.incrementAndGet())
                .count();

        assertThat(count).isEqualTo(3);
        // The JVM Stream engine elided peek() because size is known up front!
        assertThat(peekCount.get()).isEqualTo(0);
    }

    @Test
    @DisplayName("Puzzle 3: The Frozen Decrement Trap (IEEE 754 float precision loss)")
    void testFrozenDecrementTrap() {
        float bigFloat = 20_000_000.0f;
        // Single precision float only has 24 bits of mantissa (~7 decimal digits)
        // Adding or subtracting 1.0f produces identical bits!
        float afterSub = bigFloat - 1.0f;
        assertThat(afterSub == bigFloat).isTrue();
    }
}
