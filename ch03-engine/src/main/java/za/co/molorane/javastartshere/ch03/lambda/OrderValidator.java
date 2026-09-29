package za.co.molorane.javastartshere.ch03.lambda;

import java.util.Objects;

/**
 * Demonstrates the Single Abstract Method (SAM) contract of {@link FunctionalInterface}.
 * <p>
 * Key Engine Rules:
 * <ul>
 *   <li>Only abstract methods count toward the SAM contract.</li>
 *   <li>Default methods and static methods provide concrete behavior without breaking SAM eligibility.</li>
 *   <li>{@code @FunctionalInterface} instructs javac to fail at compile-time if a second abstract method is added.</li>
 * </ul>
 *
 * @param <T> the type of input to validate
 */
@FunctionalInterface
public interface OrderValidator<T> {

    /**
     * The single abstract method (SAM).
     *
     * @param input the candidate value
     * @return {@code true} if valid, {@code false} otherwise
     */
    boolean validate(T input);

    /**
     * Default method for logical AND composition.
     */
    default OrderValidator<T> and(OrderValidator<? super T> other) {
        Objects.requireNonNull(other, "other validator must not be null");
        return input -> this.validate(input) && other.validate(input);
    }

    /**
     * Default method for logical OR composition.
     */
    default OrderValidator<T> or(OrderValidator<? super T> other) {
        Objects.requireNonNull(other, "other validator must not be null");
        return input -> this.validate(input) || other.validate(input);
    }

    /**
     * Default method for logical negation.
     */
    default OrderValidator<T> negate() {
        return input -> !this.validate(input);
    }

    /**
     * Static factory creating a validator that always passes.
     */
    static <T> OrderValidator<T> alwaysValid() {
        return input -> true;
    }

    /**
     * Static factory checking for non-nullness.
     */
    static <T> OrderValidator<T> notNull() {
        return Objects::nonNull;
    }
}
