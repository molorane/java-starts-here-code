package za.co.molorane.javastartshere.ch10;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Chapter 10: In-Memory H2 Database & Flyway Schema Migration Tests")
class FlywayH2MigrationTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Should verify Flyway applied V1 and V2 migrations to H2 database")
    void shouldVerifyFlywayMigrationsRanOnH2() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String dbProduct = metaData.getDatabaseProductName();
            assertThat(dbProduct).containsIgnoringCase("H2");

            // Verify Flyway schema history table exists (Flyway 10+ defaults to quoted lowercase)
            ResultSet historyTable = metaData.getTables(null, null, "flyway_schema_history", null);
            if (!historyTable.next()) {
                historyTable = metaData.getTables(null, null, "FLYWAY_SCHEMA_HISTORY", null);
                assertThat(historyTable.next()).isTrue();
            }

            // Verify domain tables created by Flyway exist
            ResultSet clusterNodes = metaData.getTables(null, null, "CLUSTER_NODES", null);
            assertThat(clusterNodes.next()).isTrue();

            ResultSet deployments = metaData.getTables(null, null, "DEPLOYMENT_RECORDS", null);
            assertThat(deployments.next()).isTrue();
        }

        // Verify that both versioned migrations V1 and V2 are recorded
        Integer migrationCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM \"flyway_schema_history\" WHERE \"version\" IS NOT NULL AND \"success\" = TRUE", Integer.class);
        assertThat(migrationCount).isEqualTo(2);
    }

    @Test
    @DisplayName("Should perform CRUD operations on tables provisioned by Flyway")
    void shouldPerformCrudOnFlywayTables() {
        String nodeId = "node-" + UUID.randomUUID();
        Timestamp now = Timestamp.from(Instant.now());

        // Insert into cluster_nodes
        jdbcTemplate.update(
                "INSERT INTO cluster_nodes (id, hostname, status, registered_at) VALUES (?, ?, ?, ?)",
                nodeId, "k8s-worker-south-1a", "HEALTHY", now
        );

        // Query back
        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM cluster_nodes WHERE id = ?", String.class, nodeId);
        assertThat(status).isEqualTo("HEALTHY");

        // Insert into deployment_records
        jdbcTemplate.update(
                "INSERT INTO deployment_records (service_name, version, deployed_at, status) VALUES (?, ?, ?, ?)",
                "order-service", "2.5.0", now, "SUCCESS"
        );

        Integer deploymentCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM deployment_records WHERE service_name = 'order-service'", Integer.class);
        assertThat(deploymentCount).isGreaterThanOrEqualTo(1);
    }
}
