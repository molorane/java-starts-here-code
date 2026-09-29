package za.co.molorane.javastartshere.ch05;

/**
 * Demonstrates the 3-Phase Method Overload Resolution Ladder:
 * Phase 1: Primitive Widening (without boxing/varargs)
 * Phase 2: Autoboxing / Unboxing
 * Phase 3: Variable-arity (Varargs)
 */
public class OverloadingLadder {

    public static String resolve(long value) {
        return "Phase 1: Primitive Widening (long)";
    }

    public static String resolve(Integer value) {
        return "Phase 2: Autoboxing (Integer)";
    }

    public static String resolve(int... values) {
        return "Phase 3: Varargs (int...)";
    }
}
