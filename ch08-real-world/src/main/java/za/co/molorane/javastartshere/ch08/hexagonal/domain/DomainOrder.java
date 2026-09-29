package za.co.molorane.javastartshere.ch08.hexagonal.domain;

import java.util.Objects;

/**
 * Pure domain entity in Hexagonal Architecture.
 * Protects its own lifecycle invariants without any JPA or Spring annotations.
 */
public class DomainOrder {

    public enum Status {
        PENDING, PAID, CANCELLED
    }

    private final OrderId id;
    private final double totalAmount;
    private Status status;

    public DomainOrder(OrderId id, double totalAmount) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        if (totalAmount <= 0) {
            throw new IllegalArgumentException("totalAmount must be positive");
        }
        this.totalAmount = totalAmount;
        this.status = Status.PENDING;
    }

    public void completePayment() {
        if (this.status == Status.PAID) {
            throw new IllegalStateException("Order is already paid");
        }
        if (this.status == Status.CANCELLED) {
            throw new IllegalStateException("Cannot pay for a cancelled order");
        }
        this.status = Status.PAID;
    }

    public OrderId getId() { return id; }
    public double getTotalAmount() { return totalAmount; }
    public Status getStatus() { return status; }
}
