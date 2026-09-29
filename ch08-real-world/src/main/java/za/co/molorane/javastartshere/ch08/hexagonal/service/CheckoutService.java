package za.co.molorane.javastartshere.ch08.hexagonal.service;

import za.co.molorane.javastartshere.ch08.hexagonal.domain.DomainOrder;
import za.co.molorane.javastartshere.ch08.hexagonal.domain.OrderId;
import za.co.molorane.javastartshere.ch08.hexagonal.port.incoming.CheckoutUseCase;
import za.co.molorane.javastartshere.ch08.hexagonal.port.outgoing.OrderPersistencePort;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Core Domain Service implementing the incoming {@link CheckoutUseCase} port.
 * <p>
 * Notice: Pure Java. Depends strictly on domain ports and entities. Zero Spring framework annotations.
 */
public class CheckoutService implements CheckoutUseCase {

    private final OrderPersistencePort persistencePort;

    public CheckoutService(OrderPersistencePort persistencePort) {
        this.persistencePort = Objects.requireNonNull(persistencePort, "persistencePort must not be null");
    }

    @Override
    public DomainOrder checkout(OrderId id) {
        DomainOrder order = persistencePort.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found with id: " + id.value()));

        order.completePayment();
        persistencePort.save(order);
        return order;
    }
}
