package za.co.molorane.javastartshere.ch07;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.molorane.javastartshere.ch07.collections.CollectionGotchas;
import za.co.molorane.javastartshere.ch07.language.LanguageGotchas;
import za.co.molorane.javastartshere.ch07.numeric.NumericGotchas;
import za.co.molorane.javastartshere.ch07.numeric.NumericGotchas.FloatComparison;
import za.co.molorane.javastartshere.ch07.streams.StreamGotchas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Chapter 7: Java Gotchas & Puzzles Tests")
class GotchasPuzzlesTest {

    @Test
    @DisplayName("Gotcha: Integer Cache (-128 to 127) identity vs equality trap")
    void testIntegerCacheTrap() {
        Integer a = 100;
        Integer b = 100;
        assertThat(a == b).isTrue();

        Integer c = 200;
        Integer d = 200;
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

        list.remove(1); // removes index 1 ('2')
        assertThat(list).containsExactly(1, 3);

        list.remove(Integer.valueOf(1)); // removes value 1
        assertThat(list).containsExactly(3);
    }

    @Test
    @DisplayName("Puzzle 2: The Ghost in the Pipeline (Spliterator.SIZED optimization skips peek)")
    void testGhostInThePipeline() {
        AtomicInteger peekCount = new AtomicInteger(0);
        List<String> items = List.of("alpha", "beta", "gamma");

        long count = items.stream()
                .peek(item -> peekCount.incrementAndGet())
                .count();

        assertThat(count).isEqualTo(3);
        assertThat(peekCount.get()).isEqualTo(0);
    }

    @Test
    @DisplayName("Puzzle 3: The Frozen Decrement Trap (IEEE 754 float precision loss)")
    void testFrozenDecrementTrap() {
        float bigFloat = 20_000_000.0f;
        float afterSub = bigFloat - 1.0f;
        assertThat(afterSub == bigFloat).isTrue();
    }

    @Test
    @DisplayName("Gotcha: 0.1 + 0.2 != 0.3 in IEEE 754 float vs exact BigDecimal")
    void testFloatPrecisionAndBigDecimal() {
        FloatComparison comparison = NumericGotchas.evaluateFloatPrecision();

        assertThat(comparison.equalsExpected()).isFalse();
        assertThat(comparison.doubleSum()).isEqualTo(0.30000000000000004);
        assertThat(comparison.bigDecimalSum()).isEqualTo(new BigDecimal("0.3"));
    }

    @Test
    @DisplayName("Gotcha: BigDecimal.equals() considers scale, whereas compareTo() compares value")
    void testBigDecimalEqualsVsCompareTo() {
        // "2.0" (scale 1) does NOT equal "2.00" (scale 2) via equals()
        assertThat(NumericGotchas.checkBigDecimalEquality("2.0", "2.00")).isFalse();

        // But compareTo() reports numeric equivalence (returns 0)
        assertThat(NumericGotchas.compareBigDecimalValues("2.0", "2.00")).isEqualTo(0);
    }

    @Test
    @DisplayName("Gotcha: Naive comparator (a - b) overflows on Integer.MIN_VALUE vs safe Integer.compare")
    void testComparatorOverflow() {
        Comparator<Integer> naive = NumericGotchas.naiveSubtractionComparator();
        Comparator<Integer> safe = NumericGotchas.safeComparator();

        int a = Integer.MIN_VALUE;
        int b = 1;

        // Naive subtraction (MIN_VALUE - 1) overflows to positive Integer.MAX_VALUE!
        assertThat(naive.compare(a, b)).isPositive(); // WRONG: claims MIN_VALUE is greater than 1!

        // Safe comparator correctly detects a < b
        assertThat(safe.compare(a, b)).isNegative(); // CORRECT
    }

    @Test
    @DisplayName("Gotcha: Arrays.asList() mutates backing array, and ArrayStoreException on covariance")
    void testCollectionGotchas() {
        String[] original = {"A", "B", "C"};
        var result = CollectionGotchas.mutateArraysAsList(original, 1, "MODIFIED");

        assertThat(result.backingArray()[1]).isEqualTo("MODIFIED");

        assertThatThrownBy(CollectionGotchas::triggerArrayStoreException)
                .isInstanceOf(ArrayStoreException.class);
    }

    @Test
    @DisplayName("Gotcha: Stream intermediate operations are lazy and single-use")
    void testStreamLazinessAndSingleUse() {
        AtomicInteger counter = new AtomicInteger(0);
        Stream<String> stream = Stream.of("x", "y", "z");

        // No terminal operation -> 0 executions
        int executed = StreamGotchas.countWithoutTerminalOperation(stream, counter);
        assertThat(executed).isEqualTo(0);

        // Stream reuse throws IllegalStateException
        Stream<String> reusableStream = Stream.of("hello");
        assertThatThrownBy(() -> StreamGotchas.reuseConsumedStream(reusableStream))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("stream has already been operated upon or closed");
    }

    @Test
    @DisplayName("Gotcha: Post-increment reassignment traps variable, and ternary unboxing NPE")
    void testLanguagePuzzles() {
        // i = i++ keeps count at 0
        int result = LanguageGotchas.executeFrozenIncrement(50);
        assertThat(result).isEqualTo(0);

        // Ternary unboxing throws NPE when condition is false and right operand is boxed null
        assertThatThrownBy(() -> LanguageGotchas.triggerTernaryUnboxingNpe(false, null))
                .isInstanceOf(NullPointerException.class);

        // String.valueOf overload trap
        assertThat(LanguageGotchas.stringValueOfObject(null)).isEqualTo("null");
        assertThatThrownBy(() -> LanguageGotchas.stringValueOfCharArray(null))
                .isInstanceOf(NullPointerException.class);
    }
}
