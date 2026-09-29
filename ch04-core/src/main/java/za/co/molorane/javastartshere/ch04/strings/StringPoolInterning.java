package za.co.molorane.javastartshere.ch04.strings;

/**
 * Demonstrates String Constant Pool mechanics, interning, and immutability.
 * <p>
 * Key Concepts (Chapter 4 Topic 1):
 * <ul>
 *   <li><b>String Pool vs Heap:</b> String literals are stored in the JVM String Constant Pool.
 *       Using {@code new String("abc")} forces allocation of a distinct heap object.</li>
 *   <li><b>intern():</b> Ensures that identical character sequences point to the canonical pooled instance.</li>
 *   <li><b>Compact Strings (Java 9+):</b> Strings store Latin-1 characters in 1-byte {@code byte[]}
 *       arrays (coder=0) rather than 2-byte {@code char[]}, halving heap footprint.</li>
 * </ul>
 */
public class StringPoolInterning {

    /**
     * Demonstrates reference equality (==) vs logical equality (equals) across pool boundaries.
     */
    public record ComparisonResult(boolean literalSameRef, boolean heapDifferentRef, boolean internedSameRef) {}

    public static ComparisonResult compareStringAllocation() {
        String s1 = "Java25";
        String s2 = "Java25";
        String s3 = new String("Java25");
        String s4 = s3.intern();

        return new ComparisonResult(
                s1 == s2,       // true: both point to String Constant Pool literal
                s1 != s3,       // true: s3 is an explicit heap allocation
                s1 == s4        // true: s4 references the interned pool entry
        );
    }

    /**
     * Demonstrates thread-safe StringBuffer vs thread-local StringBuilder.
     */
    public static String buildTextWithBuffer(int iterations) {
        StringBuffer buffer = new StringBuffer(); // Synchronized methods
        for (int i = 0; i < iterations; i++) {
            buffer.append(i).append(":");
        }
        return buffer.toString();
    }

    public static String buildTextWithBuilder(int iterations) {
        StringBuilder builder = new StringBuilder(iterations * 4); // Unsynchronized, pre-sized
        for (int i = 0; i < iterations; i++) {
            builder.append(i).append(":");
        }
        return builder.toString();
    }
}
