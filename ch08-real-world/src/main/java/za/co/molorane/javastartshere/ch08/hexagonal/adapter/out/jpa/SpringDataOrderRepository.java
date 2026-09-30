package za.co.molorane.javastartshere.ch08.hexagonal.adapter.out.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository interface.
 * Auto-implemented by Spring Data JPA at runtime to provide standard CRUD, pagination, and sorting.
 */
@Repository
public interface SpringDataOrderRepository extends JpaRepository<OrderJpaEntity, String> {
    List<OrderJpaEntity> findByCustomerId(String customerId);
}
