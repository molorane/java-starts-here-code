package za.co.molorane.javastartshere.ch12;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.molorane.javastartshere.ch12.adapter.InMemoryInvoiceRepository;
import za.co.molorane.javastartshere.ch12.adapter.InvoiceController;
import za.co.molorane.javastartshere.ch12.application.InvoiceService;
import za.co.molorane.javastartshere.ch12.domain.DomainInvoice;
import za.co.molorane.javastartshere.ch12.domain.InvoiceStatus;
import za.co.molorane.javastartshere.ch12.resilience.BoundedExecutorFactory;
import za.co.molorane.javastartshere.ch12.resilience.ProductionHttpClient;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.concurrent.ThreadPoolExecutor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Chapter 12: Production Reality & Resilience Defense Tests")
class ProductionRealityTest {

    @Test
    @DisplayName("Should execute domain logic through hexagonal architecture layers cleanly")
    void shouldExecuteDomainFlowThroughHexagonalLayers() {
        InMemoryInvoiceRepository repository = new InMemoryInvoiceRepository();
        InvoiceService service = new InvoiceService(repository);
        InvoiceController controller = new InvoiceController(service);

        DomainInvoice created = controller.handleInvoiceCreation("INV-2026-001", 500.00);
        assertThat(created.getStatus()).isEqualTo(InvoiceStatus.DRAFT);
        assertThat(created.getAmount()).isEqualTo(500.00);

        DomainInvoice issued = service.issueInvoice("INV-2026-001");
        assertThat(issued.getStatus()).isEqualTo(InvoiceStatus.ISSUED);

        issued.markPaid();
        assertThat(issued.getStatus()).isEqualTo(InvoiceStatus.PAID);
    }

    @Test
    @DisplayName("Should enforce bounded queue in ThreadPool to counter the Vibe Coding OOM memory bomb")
    void shouldEnforceBoundedThreadPool() {
        int poolSize = 4;
        int queueCapacity = 100;
        ThreadPoolExecutor executor = BoundedExecutorFactory.createThrottlingPool(poolSize, queueCapacity);

        try {
            assertThat(executor.getCorePoolSize()).isEqualTo(poolSize);
            assertThat(executor.getMaximumPoolSize()).isEqualTo(poolSize);
            // Verify queue is strictly bounded and NOT Integer.MAX_VALUE!
            assertThat(executor.getQueue().remainingCapacity()).isEqualTo(queueCapacity);
            assertThat(executor.getQueue().remainingCapacity()).isLessThan(Integer.MAX_VALUE);
            assertThat(executor.getRejectedExecutionHandler()).isInstanceOf(ThreadPoolExecutor.CallerRunsPolicy.class);
        } finally {
            executor.shutdownNow();
        }

        // Factory must reject unbounded capacities (Integer.MAX_VALUE or <= 0)
        assertThatThrownBy(() -> BoundedExecutorFactory.createThrottlingPool(4, Integer.MAX_VALUE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Queue capacity must be strictly bounded");
    }

    @Test
    @DisplayName("Should enforce non-null positive timeouts to counter the Vibe Coding hung thread trap")
    void shouldEnforceHttpClientTimeouts() {
        Duration connectTimeout = Duration.ofSeconds(5);
        Duration requestTimeout = Duration.ofSeconds(10);

        ProductionHttpClient client = new ProductionHttpClient(connectTimeout, requestTimeout);

        assertThat(client.getClient().connectTimeout()).contains(connectTimeout);
        assertThat(client.getRequestTimeout()).isEqualTo(requestTimeout);

        HttpRequest request = client.createGetRequest(URI.create("https://api.example.com/health"));
        assertThat(request.timeout()).contains(requestTimeout);

        // Client must reject zero or negative timeouts
        assertThatThrownBy(() -> new ProductionHttpClient(Duration.ZERO, requestTimeout))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("connectTimeout must be strictly positive");

        assertThatThrownBy(() -> new ProductionHttpClient(connectTimeout, Duration.ofSeconds(-1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("requestTimeout must be strictly positive");
    }
}
