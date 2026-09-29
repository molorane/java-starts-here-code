package za.co.molorane.javastartshere.ch08.hexagonal.adapter.out;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import za.co.molorane.javastartshere.ch08.hexagonal.domain.DomainOrder;
import za.co.molorane.javastartshere.ch08.hexagonal.domain.OrderId;
import za.co.molorane.javastartshere.ch08.hexagonal.port.outgoing.OrderPersistencePort;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Driven (outbound) persistence adapter backed by H2 in-memory relational database.
 * Table schema and constraints are version-controlled and applied via Liquibase changelogs.
 */
@Repository
public class H2OrderRepositoryAdapter implements OrderPersistencePort {

    private final JdbcTemplate jdbcTemplate;

    public H2OrderRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate, "jdbcTemplate cannot be null");
    }

    @Override
    public Optional<DomainOrder> findById(OrderId id) {
        String sql = "SELECT id, amount, status FROM orders WHERE id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            OrderId orderId = new OrderId(rs.getString("id"));
            double amount = rs.getDouble("amount");
            DomainOrder order = new DomainOrder(orderId, amount);
            String status = rs.getString("status");
            if ("PAID".equalsIgnoreCase(status)) {
                order.completePayment();
            }
            return order;
        }, id.value()).stream().findFirst();
    }

    @Override
    public void save(DomainOrder order) {
        String mergeSql = "MERGE INTO orders (id, customer_id, amount, status, created_at) KEY(id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(mergeSql,
                order.getId().value(),
                "GUEST",
                order.getTotalAmount(),
                order.getStatus().name(),
                Timestamp.from(Instant.now())
        );
    }
}
