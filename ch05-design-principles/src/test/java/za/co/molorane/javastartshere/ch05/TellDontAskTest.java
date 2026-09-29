package za.co.molorane.javastartshere.ch05;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.molorane.javastartshere.ch05.telldontask.RichOrderModel.Order;
import za.co.molorane.javastartshere.ch05.telldontask.RichOrderModel.OrderStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Chapter 5: Tell Don't Ask & Invariant Protection Tests")
class TellDontAskTest {

    @Test
    @DisplayName("Rich order model enforces state transitions and guards invariants")
    void shouldFollowValidOrderLifecycle() {
        Order order = new Order("ORD-881");
        order.addLine("BOOK-JAVA", 2, 49.99);
        order.confirm();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);

        order.ship();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);

        // Invariant guard: Shipped order cannot be cancelled
        assertThatThrownBy(() -> order.cancel("Customer changed mind"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already shipped");
    }

    @Test
    @DisplayName("Cannot confirm an order with zero lines")
    void shouldRejectEmptyOrderConfirmation() {
        Order emptyOrder = new Order("ORD-EMPTY");

        assertThatThrownBy(emptyOrder::confirm)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("zero lines");
    }
}
