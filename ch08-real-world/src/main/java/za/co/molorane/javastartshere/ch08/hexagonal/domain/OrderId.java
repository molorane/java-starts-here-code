package za.co.molorane.javastartshere.ch08.hexagonal.domain;

import java.util.Objects;

/**
 * Pure domain value object. ZERO framework annotations, ZERO external dependencies.
 */
public record OrderId(String value) {
    public OrderId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("OrderId cannot be blank");
        }
    }
}
