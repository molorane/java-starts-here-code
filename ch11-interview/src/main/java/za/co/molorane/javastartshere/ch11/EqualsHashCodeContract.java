package za.co.molorane.javastartshere.ch11;

import java.util.Objects;

/**
 * Demonstrates the equals() and hashCode() contract under pressure (JLS §3.12):
 * 1. Missing hashCode() trap: leads to silent data loss in HashSets and HashMaps.
 * 2. The 5 formal mathematical properties:
 *    - Reflexive: x.equals(x) is true.
 *    - Symmetric: x.equals(y) == y.equals(x).
 *    - Transitive: if x.equals(y) && y.equals(z), then x.equals(z).
 *    - Consistent: multiple calls return the same result.
 *    - Non-nullity: x.equals(null) is always false.
 */
public class EqualsHashCodeContract {

    /**
     * DANGEROUS ANTI-PATTERN: Overrides equals() but forgets hashCode()!
     * Inherits Object.hashCode() (identity based), causing HashSet.contains() to return false.
     */
    public static class BrokenUser {
        private final String userId;

        public BrokenUser(String userId) {
            this.userId = Objects.requireNonNull(userId);
        }

        public String getUserId() {
            return userId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BrokenUser other)) return false;
            return Objects.equals(userId, other.userId);
        }

        // Deliberately omits hashCode()!
    }

    /**
     * Proper, robust implementation obeying the contract.
     */
    public record CorrectUser(String userId) {
        public CorrectUser {
            Objects.requireNonNull(userId);
        }
    }
}
