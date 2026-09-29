package za.co.molorane.javastartshere.ch08.hexagonal.port.outgoing;

import za.co.molorane.javastartshere.ch08.hexagonal.domain.DomainOrder;
import za.co.molorane.javastartshere.ch08.hexagonal.domain.OrderId;

import java.util.Optional;

/**
 * Outgoing (Driven) Port declaring what persistence capability the core domain requires.
 */
public interface OrderPersistencePort {
    Optional<DomainOrder> findById(OrderId id);
    void save(DomainOrder order);
}
