package za.co.molorane.javastartshere.ch11;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Demonstrates internal mechanics and common traps of {@link java.util.HashMap}:
 * 1. Bitwise index computation: {@code (capacity - 1) & (h ^ (h >>> 16))}.
 * 2. Hash collisions and equals resolution.
 * 3. The Mutable Key Trap: mutating a key after insertion loses the entry in the map.
 */
public class HashMapDeepMechanics {

    /**
     * HotSpot HashMap hash perturbation function:
     * Spreads higher bits of hash code to lower bits to prevent clustering.
     */
    public static int hash(Object key) {
        if (key == null) {
            return 0;
        }
        int h = key.hashCode();
        return h ^ (h >>> 16);
    }

    /**
     * Bitwise bucket index computation used by HashMap.
     * Equivalent to {@code hash % capacity} only when capacity is a power of two.
     */
    public static int computeBucketIndex(Object key, int capacity) {
        if ((capacity & (capacity - 1)) != 0) {
            throw new IllegalArgumentException("Capacity must be a power of two: " + capacity);
        }
        return (capacity - 1) & hash(key);
    }

    /**
     * Demonstrates the Mutable Key Trap where a mutated key can never be found again.
     */
    public static class MutableKey {
        private String value;

        public MutableKey(String value) {
            this.value = value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MutableKey other)) return false;
            return Objects.equals(value, other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }
}
