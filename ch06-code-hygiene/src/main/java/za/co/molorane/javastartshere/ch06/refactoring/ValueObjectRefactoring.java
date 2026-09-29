package za.co.molorane.javastartshere.ch06.refactoring;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Demonstrates the Primitive Obsession code smell and its surgical cure using Java Records as Value Objects.
 * <p>
 * Key Concepts (Chapter 6 Topic 7):
 * <ul>
 *   <li><b>Primitive Obsession:</b> Representing rich domain concepts using raw primitives (e.g. String, BigDecimal)
 *       leads to scattered validation and dangerous parameter transposition bugs.</li>
 *   <li><b>Value Objects:</b> Immutable, self-validating types. Guaranteed correct by construction.</li>
 * </ul>
 */
public class ValueObjectRefactoring {

    public record EmailAddress(String value) {
        private static final Pattern PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

        public EmailAddress {
            if (value == null || !PATTERN.matcher(value).matches()) {
                throw new IllegalArgumentException("Invalid email format: " + value);
            }
        }
    }

    public record Money(BigDecimal amount, String currency) {
        public Money {
            Objects.requireNonNull(amount, "amount must not be null");
            Objects.requireNonNull(currency, "currency must not be null");
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Money amount cannot be negative");
            }
            if (currency.length() != 3) {
                throw new IllegalArgumentException("Currency must be a 3-letter ISO code: " + currency);
            }
        }

        public Money add(Money other) {
            if (!this.currency.equals(other.currency)) {
                throw new IllegalArgumentException("Cannot add distinct currencies: " + currency + " vs " + other.currency);
            }
            return new Money(this.amount.add(other.amount), this.currency);
        }
    }

    public record NationalId(String idNumber) {
        public NationalId {
            if (idNumber == null || idNumber.length() < 6) {
                throw new IllegalArgumentException("National ID must be at least 6 characters");
            }
        }
    }

    public record UserProfile(EmailAddress email, NationalId nationalId, Money creditLimit) {}

    /**
     * Type-safe registration service: Impossible for the caller to accidentally transpose email and nationalId.
     */
    public static UserProfile registerUser(EmailAddress email, NationalId nationalId, Money creditLimit) {
        return new UserProfile(email, nationalId, creditLimit);
    }
}
