package za.co.molorane.javastartshere.ch12.domain;

import java.util.Objects;

/**
 * Strongly typed Invoice Identifier value object.
 */
public record InvoiceId(String value) {
    public InvoiceId {
        Objects.requireNonNull(value, "InvoiceId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("InvoiceId value cannot be blank");
        }
    }
}
