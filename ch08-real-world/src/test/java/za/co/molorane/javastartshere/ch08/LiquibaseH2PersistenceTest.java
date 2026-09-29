package za.co.molorane.javastartshere.ch08;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import za.co.molorane.javastartshere.ch08.hexagonal.adapter.out.H2OrderRepositoryAdapter;
import za.co.molorane.javastartshere.ch08.hexagonal.domain.DomainOrder;
import za.co.molorane.javastartshere.ch08.hexagonal.domain.OrderId;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Chapter 8: In-Memory H2 Database & Liquibase Migration Integration Tests")
class LiquibaseH2PersistenceTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Should verify Liquibase executed migrations and created schema in H2 database")
    void shouldVerifyLiquibaseMigrationRanOnH2() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String dbProduct = metaData.getDatabaseProductName();
            assertThat(dbProduct).containsIgnoringCase("H2");

            // Verify Liquibase tables exist
            ResultSet tables = metaData.getTables(null, null, "DATABASECHANGELOG", null);
            assertThat(tables.next()).isTrue();

            // Verify our domain table created by Liquibase exists
            ResultSet orderTable = metaData.getTables(null, null, "ORDERS", null);
            assertThat(orderTable.next()).isTrue();
        }

        // Query Liquibase changelog table
        Integer changeSetCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM DATABASECHANGELOG WHERE ID = '001-create-orders-table'", Integer.class);
        assertThat(changeSetCount).isEqualTo(1);
    }

    @Test
    @DisplayName("Should persist and retrieve domain order using H2OrderRepositoryAdapter")
    void shouldPersistOrderThroughH2Adapter() {
        H2OrderRepositoryAdapter adapter = new H2OrderRepositoryAdapter(jdbcTemplate);
        OrderId orderId = new OrderId(UUID.randomUUID().toString());
        DomainOrder order = new DomainOrder(orderId, 199.95);

        // 1. Save
        adapter.save(order);

        // 2. Query back
        Optional<DomainOrder> retrieved = adapter.findById(orderId);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getId()).isEqualTo(orderId);
        assertThat(retrieved.get().getTotalAmount()).isEqualTo(199.95);
        assertThat(retrieved.get().getStatus()).isEqualTo(DomainOrder.Status.PENDING);

        // 3. Update payment and save
        order.completePayment();
        adapter.save(order);

        Optional<DomainOrder> paidOrder = adapter.findById(orderId);
        assertThat(paidOrder).isPresent();
        assertThat(paidOrder.get().getStatus()).isEqualTo(DomainOrder.Status.PAID);
    }
}
