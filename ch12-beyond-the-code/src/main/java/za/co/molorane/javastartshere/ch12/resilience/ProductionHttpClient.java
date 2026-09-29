package za.co.molorane.javastartshere.ch12.resilience;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Objects;

/**
 * Defensive HTTP Client Builder countering the "Vibe Coding" Missing Timeout Trap.
 *
 * Prevents thread pool starvation and hung worker threads by enforcing strict,
 * non-null connection and request timeouts.
 */
public final class ProductionHttpClient {

    private final HttpClient client;
    private final Duration requestTimeout;

    public ProductionHttpClient(Duration connectTimeout, Duration requestTimeout) {
        Objects.requireNonNull(connectTimeout, "connectTimeout cannot be null");
        Objects.requireNonNull(requestTimeout, "requestTimeout cannot be null");

        if (connectTimeout.isZero() || connectTimeout.isNegative()) {
            throw new IllegalArgumentException("connectTimeout must be strictly positive");
        }
        if (requestTimeout.isZero() || requestTimeout.isNegative()) {
            throw new IllegalArgumentException("requestTimeout must be strictly positive");
        }

        this.client = HttpClient.newBuilder()
                .connectTimeout(connectTimeout)
                .build();
        this.requestTimeout = requestTimeout;
    }

    public HttpRequest createGetRequest(URI uri) {
        return HttpRequest.newBuilder()
                .uri(Objects.requireNonNull(uri, "URI cannot be null"))
                .timeout(requestTimeout)
                .GET()
                .build();
    }

    public HttpClient getClient() {
        return client;
    }

    public Duration getRequestTimeout() {
        return requestTimeout;
    }
}
