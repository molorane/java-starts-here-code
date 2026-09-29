package za.co.molorane.javastartshere.ch07.language;

/**
 * Demonstrates subtle Java language puzzles, operator mechanics, and auto-unboxing traps.
 * <p>
 * Key Concepts (Chapter 7 Topics 1, 3, 6):
 * <ul>
 *   <li><b>Post-Increment Reassignment:</b> In {@code i = i++}, the original value of {@code i} is
 *       placed on the operand stack before incrementing. The assignment then overwrites the incremented
 *       value, freezing {@code i} at its initial value.</li>
 *   <li><b>Ternary Unboxing NPE:</b> When a ternary operator combines a primitive with a boxed wrapper,
 *       the JLS mandates numeric type promotion, forcing unboxing. If the wrapper is null, an unexpected
 *       {@link NullPointerException} occurs.</li>
 *   <li><b>String.valueOf Overload Ambiguity:</b> {@code String.valueOf((char[]) null)} throws
 *       {@link NullPointerException}, whereas {@code String.valueOf((Object) null)} returns {@code "null"}.</li>
 * </ul>
 */
public class LanguageGotchas {

    /**
     * Demonstrates post-increment reassignment loop trap.
     */
    public static int executeFrozenIncrement(int iterations) {
        int count = 0;
        for (int i = 0; i < iterations; i++) {
            count = count++; // Traps count at 0 every iteration!
        }
        return count;
    }

    /**
     * Demonstrates ternary unboxing NullPointerException.
     * JLS §15.25: If one operand is of type Double and the other is double, the ternary expression type is double.
     * The null Double is auto-unboxed, throwing NPE!
     */
    public static double triggerTernaryUnboxingNpe(boolean condition, Double boxedNull) {
        return condition ? 1.0 : boxedNull; // When false, unboxes boxedNull -> NPE!
    }

    /**
     * Demonstrates String.valueOf null pointer trap.
     */
    public static String stringValueOfObject(Object obj) {
        return String.valueOf(obj); // Returns "null"
    }

    public static String stringValueOfCharArray(char[] chars) {
        return String.valueOf(chars); // Throws NPE if chars is null!
    }
}
