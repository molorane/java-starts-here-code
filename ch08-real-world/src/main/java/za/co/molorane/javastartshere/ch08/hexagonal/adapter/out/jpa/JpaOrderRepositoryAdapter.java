package za.co.molorane.javastartshere.ch08.hexagonal.adapter.out.jpa;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import za.co.molorane.javastartshere.ch08.hexagonal.domain.DomainOrder;
import za.co.molorane.javastartshere.ch08.hexagonal.domain.OrderId;
import za.co.molorane.javastartshere.ch08.hexagonal.port.outgoing.OrderPersistencePort;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Driven (outbound) persistence adapter backed by Spring Data JPA.
 * Bridges domain entity and JPA database entity, decoupling the domain from Hibernate and JPA.
 */
@Repository
@Primary
public class JpaOrderRepositoryAdapter implements OrderPersistencePort {

    private final SpringDataOrderRepository springDataRepository;

    public JpaOrderRepositoryAdapter(SpringDataOrderRepository springDataRepository) {
        this.springDataRepository = Objects.requireNonNull(springDataRepository, "springDataRepository cannot be null");
    }

    @Override
    public Optional<DomainOrder> findById(OrderId id) {
        return springDataRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public void save(DomainOrder order) {
        OrderJpaEntity entity = toEntity(order);
        springDataRepository.save(entity);
    }

    private DomainOrder toDomain(OrderJpaEntity entity) {
        DomainOrder domain = new DomainOrder(new OrderId(entity.getId()), entity.getAmount().doubleValue());
        if ("PAID".equalsIgnoreCase(entity.getStatus())) {
            domain.completePayment();
        }
        return domain;
    }

    private OrderJpaEntity toEntity(DomainOrder domain) {
        return new OrderJpaEntity(
                domain.getId().value(),
                "GUEST",
                BigDecimal.valueOf(domain.getTotalAmount()),
                domain.getStatus().name(),
                Instant.now()
        );
    }
}
