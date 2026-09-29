package za.co.molorane.javastartshere.ch05.telldontask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Demonstrates the "Tell, Don't Ask" principle and Anti-Anemic Domain Modeling.
 * <p>
 * Key Concepts (Chapter 5 Topic 8):
 * <ul>
 *   <li><b>Anemic Anti-Pattern:</b> Entities reduced to bare getters and setters, with business logic
 *       scattered across procedural service classes. Invariants are easily bypassed.</li>
 *   <li><b>Tell, Don't Ask:</b> Objects encapsulate both state and behavior. Callers invoke semantic commands
 *       (e.g., {@code order.cancel()}) and the entity enforces its own lifecycle invariants.</li>
 * </ul>
 */
public class RichOrderModel {

    public enum OrderStatus {
        CREATED, CONFIRMED, SHIPPED, CANCELLED
    }

    public record OrderLine(String sku, int quantity, double unitPrice) {}

    /**
     * RICH DOMAIN MODEL: Encapsulates invariants and state transitions.
     */
    public static class Order {
        private final String orderId;
        private final List<OrderLine> lines = new ArrayList<>();
        private OrderStatus status = OrderStatus.CREATED;

        public Order(String orderId) {
            if (orderId == null || orderId.isBlank()) {
                throw new IllegalArgumentException("orderId must not be blank");
            }
            this.orderId = orderId;
        }

        public void addLine(String sku, int quantity, double unitPrice) {
            if (status != OrderStatus.CREATED) {
                throw new IllegalStateException("Cannot add items to an order that is " + status);
            }
            if (quantity <= 0 || unitPrice <= 0) {
                throw new IllegalArgumentException("Quantity and price must be positive");
            }
            lines.add(new OrderLine(sku, quantity, unitPrice));
        }

        public void confirm() {
            if (status != OrderStatus.CREATED) {
                throw new IllegalStateException("Order must be in CREATED state to confirm");
            }
            if (lines.isEmpty()) {
                throw new IllegalStateException("Cannot confirm an order with zero lines");
            }
            this.status = OrderStatus.CONFIRMED;
        }

        public void ship() {
            if (status != OrderStatus.CONFIRMED) {
                throw new IllegalStateException("Only confirmed orders can be shipped");
            }
            this.status = OrderStatus.SHIPPED;
        }

        public void cancel(String reason) {
            if (status == OrderStatus.SHIPPED) {
                throw new IllegalStateException("Cannot cancel an order that has already shipped");
            }
            this.status = OrderStatus.CANCELLED;
        }

        public String getOrderId() { return orderId; }
        public OrderStatus getStatus() { return status; }
        public List<OrderLine> getLines() { return Collections.unmodifiableList(lines); }
    }
}
