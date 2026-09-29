package za.co.molorane.javastartshere.ch12.domain;

import java.util.Objects;

/**
 * Pure domain model: ZERO framework dependencies, ZERO annotations.
 * Encapsulates domain invariants and lifecycle state transitions.
 */
public class DomainInvoice {

    private final InvoiceId id;
    private final double amount;
    private InvoiceStatus status;

    public DomainInvoice(InvoiceId id, double amount) {
        this.id = Objects.requireNonNull(id, "InvoiceId cannot be null");
        if (amount <= 0) {
            throw new IllegalArgumentException("Invoice amount must be strictly positive: " + amount);
        }
        this.amount = amount;
        this.status = InvoiceStatus.DRAFT;
    }

    public void issue() {
        if (this.status != InvoiceStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT invoices can be issued. Current: " + status);
        }
        this.status = InvoiceStatus.ISSUED;
    }

    public void markPaid() {
        if (this.status != InvoiceStatus.ISSUED) {
            throw new IllegalStateException("Only ISSUED invoices can be paid. Current: " + status);
        }
        this.status = InvoiceStatus.PAID;
    }

    public InvoiceId getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public InvoiceStatus getStatus() {
        return status;
    }
}
