package za.co.molorane.javastartshere.ch07.numeric;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * Demonstrates Java numeric gotchas, IEEE 754 quirks, and comparator subtraction traps.
 * <p>
 * Key Concepts (Chapter 7 Topics 1, 3, 4):
 * <ul>
 *   <li><b>Integer Caching:</b> Autoboxing uses {@code IntegerCache} (-128 to 127). Values outside
 *       this range allocate distinct heap objects, breaking reference equality ({@code ==}).</li>
 *   <li><b>Binary Floating-Point:</b> Binary representation cannot represent 0.1 or 0.2 exactly.
 *       {@code 0.1 + 0.2 != 0.3}. Always use {@code BigDecimal(String)} for monetary arithmetic.</li>
 *   <li><b>BigDecimal equals() vs compareTo():</b> {@code equals()} requires identical scale
 *       ({@code 2.0} does not equal {@code 2.00}), whereas {@code compareTo()} compares numeric value.</li>
 *   <li><b>Comparator Subtraction Overflow:</b> {@code (a, b) -> a - b} overflows when evaluating
 *       {@code Integer.MIN_VALUE - 1}, inverting the sort order. Always use {@link Integer#compare(int, int)}.</li>
 * </ul>
 */
public class NumericGotchas {

    public record FloatComparison(double doubleSum, boolean equalsExpected, BigDecimal bigDecimalSum) {}

    public static FloatComparison evaluateFloatPrecision() {
        double sum = 0.1 + 0.2; // 0.30000000000000004
        boolean equals = (sum == 0.3);

        BigDecimal d1 = new BigDecimal("0.1");
        BigDecimal d2 = new BigDecimal("0.2");
        BigDecimal bdSum = d1.add(d2);

        return new FloatComparison(sum, equals, bdSum);
    }

    public static boolean checkBigDecimalEquality(String val1, String val2) {
        BigDecimal b1 = new BigDecimal(val1);
        BigDecimal b2 = new BigDecimal(val2);
        return b1.equals(b2);
    }

    public static int compareBigDecimalValues(String val1, String val2) {
        BigDecimal b1 = new BigDecimal(val1);
        BigDecimal b2 = new BigDecimal(val2);
        return b1.compareTo(b2);
    }

    /**
     * Vulnerable comparator: susceptible to integer overflow.
     */
    public static Comparator<Integer> naiveSubtractionComparator() {
        return (a, b) -> a - b;
    }

    /**
     * Safe comparator: uses Integer.compare.
     */
    public static Comparator<Integer> safeComparator() {
        return Integer::compare;
    }
}
