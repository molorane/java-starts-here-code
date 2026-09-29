package za.co.molorane.javastartshere.ch04.generics;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Demonstrates Generics, PECS (Producer Extends, Consumer Super), and Super Type Tokens.
 * <p>
 * Key Concepts (Chapter 4 Topic 5):
 * <ul>
 *   <li><b>PECS Principle:</b>
 *       Use {@code ? extends T} when reading items out of a collection (Producer).
 *       Use {@code ? super T} when adding items into a collection (Consumer).</li>
 *   <li><b>Super Type Token:</b> Overcoming Type Erasure at runtime by capturing generic parameters
 *       from an anonymous inner subclass via {@link Class#getGenericSuperclass()}.</li>
 * </ul>
 */
public class PecsGenericsPipeline {

    public static class Transaction {
        private final double amount;
        public Transaction(double amount) { this.amount = amount; }
        public double getAmount() { return amount; }
    }

    public static class PriorityTransaction extends Transaction {
        public PriorityTransaction(double amount) { super(amount); }
    }

    /**
     * Canonical PECS method:
     * - src is a PRODUCER: provides items of type T or subtypes (? extends T).
     * - dest is a CONSUMER: accepts items of type T or supertypes (? super T).
     */
    public static <T> void copy(List<? extends T> src, List<? super T> dest) {
        for (T item : src) {
            dest.add(item);
        }
    }

    /**
     * Neal Gafter's Super Type Token pattern to outsmart type erasure.
     */
    public static abstract class TypeReference<T> {
        private final Type type;

        protected TypeReference() {
            Type superclass = getClass().getGenericSuperclass();
            if (superclass instanceof ParameterizedType parameterized) {
                this.type = parameterized.getActualTypeArguments()[0];
            } else {
                throw new IllegalArgumentException("TypeReference must be parameterized");
            }
        }

        public Type getType() {
            return type;
        }
    }
}
