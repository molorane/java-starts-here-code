package za.co.molorane.javastartshere.ch08;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.co.molorane.javastartshere.ch08.hexagonal.adapter.out.jpa.JpaOrderRepositoryAdapter;
import za.co.molorane.javastartshere.ch08.hexagonal.adapter.out.jpa.OrderJpaEntity;
import za.co.molorane.javastartshere.ch08.hexagonal.adapter.out.jpa.SpringDataOrderRepository;
import za.co.molorane.javastartshere.ch08.hexagonal.domain.DomainOrder;
import za.co.molorane.javastartshere.ch08.hexagonal.domain.OrderId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Chapter 8: Spring Data JPA & Hibernate Validation Integration Tests")
class SpringDataJpaPersistenceTest {

    @Autowired
    private JpaOrderRepositoryAdapter jpaOrderRepositoryAdapter;

    @Autowired
    private SpringDataOrderRepository springDataOrderRepository;

    @Test
    @DisplayName("Should persist and find domain orders through JpaOrderRepositoryAdapter")
    void shouldPersistDomainOrderViaJpaAdapter() {
        OrderId orderId = new OrderId(UUID.randomUUID().toString());
        DomainOrder order = new DomainOrder(orderId, 249.99);

        // 1. Save via domain port implementation
        jpaOrderRepositoryAdapter.save(order);

        // 2. Retrieve via domain port implementation
        Optional<DomainOrder> retrieved = jpaOrderRepositoryAdapter.findById(orderId);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getId()).isEqualTo(orderId);
        assertThat(retrieved.get().getTotalAmount()).isEqualTo(249.99);
        assertThat(retrieved.get().getStatus()).isEqualTo(DomainOrder.Status.PENDING);

        // 3. Mark paid and verify persistence update
        order.completePayment();
        jpaOrderRepositoryAdapter.save(order);

        Optional<DomainOrder> paidOrder = jpaOrderRepositoryAdapter.findById(orderId);
        assertThat(paidOrder).isPresent();
        assertThat(paidOrder.get().getStatus()).isEqualTo(DomainOrder.Status.PAID);
    }

    @Test
    @DisplayName("Should execute custom derived query method on Spring Data JPA repository")
    void shouldQueryViaSpringDataDerivedMethod() {
        String testCustomerId = "CUST-" + UUID.randomUUID();
        String orderId1 = UUID.randomUUID().toString();
        String orderId2 = UUID.randomUUID().toString();

        springDataOrderRepository.save(new OrderJpaEntity(orderId1, testCustomerId, java.math.BigDecimal.valueOf(80.00), "PENDING", Instant.now()));
        springDataOrderRepository.save(new OrderJpaEntity(orderId2, testCustomerId, java.math.BigDecimal.valueOf(120.00), "CONFIRMED", Instant.now()));

        List<OrderJpaEntity> customerOrders = springDataOrderRepository.findByCustomerId(testCustomerId);
        assertThat(customerOrders).hasSize(2);
        assertThat(customerOrders).extracting(OrderJpaEntity::getId).containsExactlyInAnyOrder(orderId1, orderId2);
    }
}
