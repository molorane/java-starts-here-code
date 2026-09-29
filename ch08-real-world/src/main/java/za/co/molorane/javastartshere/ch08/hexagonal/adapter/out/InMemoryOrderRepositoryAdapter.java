package za.co.molorane.javastartshere.ch08.hexagonal.adapter.out;

import org.springframework.stereotype.Repository;
import za.co.molorane.javastartshere.ch08.hexagonal.domain.DomainOrder;
import za.co.molorane.javastartshere.ch08.hexagonal.domain.OrderId;
import za.co.molorane.javastartshere.ch08.hexagonal.port.outgoing.OrderPersistencePort;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Outgoing Infrastructure Adapter implementing the {@link OrderPersistencePort}.
 * Framework annotations belong out here on the adapter boundary!
 */
@Repository
public class InMemoryOrderRepositoryAdapter implements OrderPersistencePort {

    private final Map<OrderId, DomainOrder> database = new ConcurrentHashMap<>();

    @Override
    public Optional<DomainOrder> findById(OrderId id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public void save(DomainOrder order) {
        database.put(order.getId(), order);
    }

    public void clear() {
        database.clear();
    }
}
