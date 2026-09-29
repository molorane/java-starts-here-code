package za.co.molorane.javastartshere.ch12.domain;

/**
 * Pure domain model: ZERO framework dependencies, ZERO annotations.
 */
public record DomainInvoice(String invoiceId, double amount) {
    public DomainInvoice {
        if (amount <= 0) {
            throw new IllegalArgumentException("Invoice amount must be positive");
        }
    }
}
