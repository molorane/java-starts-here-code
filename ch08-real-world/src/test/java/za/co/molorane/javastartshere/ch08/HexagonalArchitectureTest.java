package za.co.molorane.javastartshere.ch08;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.molorane.javastartshere.ch08.hexagonal.adapter.out.InMemoryOrderRepositoryAdapter;
import za.co.molorane.javastartshere.ch08.hexagonal.domain.DomainOrder;
import za.co.molorane.javastartshere.ch08.hexagonal.domain.DomainOrder.Status;
import za.co.molorane.javastartshere.ch08.hexagonal.domain.OrderId;
import za.co.molorane.javastartshere.ch08.hexagonal.port.incoming.CheckoutUseCase;
import za.co.molorane.javastartshere.ch08.hexagonal.service.CheckoutService;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Chapter 8: Hexagonal Architecture (Ports and Adapters) Tests")
class HexagonalArchitectureTest {

    private InMemoryOrderRepositoryAdapter repositoryAdapter;
    private CheckoutUseCase checkoutUseCase;

    @BeforeEach
    void setUp() {
        repositoryAdapter = new InMemoryOrderRepositoryAdapter();
        checkoutUseCase = new CheckoutService(repositoryAdapter);
    }

    @Test
    @DisplayName("Should successfully checkout pending order and update status via incoming and outgoing ports")
    void shouldExecuteCheckoutFlowThroughPorts() {
        OrderId id = new OrderId("ORD-HEX-101");
        DomainOrder order = new DomainOrder(id, 149.99);
        repositoryAdapter.save(order);

        DomainOrder processed = checkoutUseCase.checkout(id);

        assertThat(processed.getStatus()).isEqualTo(Status.PAID);
        assertThat(repositoryAdapter.findById(id).orElseThrow().getStatus()).isEqualTo(Status.PAID);
    }

    @Test
    @DisplayName("Should throw NoSuchElementException when order does not exist")
    void shouldThrowWhenOrderNotFound() {
        OrderId missingId = new OrderId("NON-EXISTENT");

        assertThatThrownBy(() -> checkoutUseCase.checkout(missingId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("NON-EXISTENT");
    }

    @Test
    @DisplayName("Domain entity should protect its own invariant against paying an already paid order")
    void shouldPreventDoublePaymentOnDomainEntity() {
        OrderId id = new OrderId("ORD-HEX-PAID");
        DomainOrder order = new DomainOrder(id, 99.0);
        order.completePayment(); // Already paid
        repositoryAdapter.save(order);

        assertThatThrownBy(() -> checkoutUseCase.checkout(id))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already paid");
    }
}
