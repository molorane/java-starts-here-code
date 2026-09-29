package za.co.molorane.javastartshere.ch08.hexagonal.port.incoming;

import za.co.molorane.javastartshere.ch08.hexagonal.domain.DomainOrder;
import za.co.molorane.javastartshere.ch08.hexagonal.domain.OrderId;

/**
 * Incoming (Driving) Port declaring what the application core can do for external callers.
 */
public interface CheckoutUseCase {
    DomainOrder checkout(OrderId id);
}
