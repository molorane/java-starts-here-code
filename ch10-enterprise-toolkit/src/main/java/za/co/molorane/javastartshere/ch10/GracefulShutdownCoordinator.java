package za.co.molorane.javastartshere.ch10;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Simulates and coordinates Kubernetes-aware zero-downtime graceful shutdown.
 * - Tracks in-flight HTTP transactions.
 * - Stops accepting new traffic immediately upon SIGTERM / shutdown signal.
 * - Drains active transactions within a bounded timeout window before allowing the container to terminate.
 */
public class GracefulShutdownCoordinator {

    public enum LifecycleState {
        ACCEPTING_TRAFFIC,
        DRAINING,
        STOPPED
    }

    public static class ServiceUnavailableException extends RuntimeException {
        public ServiceUnavailableException(String message) {
            super(message);
        }
    }

    private final AtomicReference<LifecycleState> state = new AtomicReference<>(LifecycleState.ACCEPTING_TRAFFIC);
    private final AtomicInteger inFlightRequests = new AtomicInteger(0);
    private final Object drainLock = new Object();

    public LifecycleState getState() {
        return state.get();
    }

    public int getInFlightCount() {
        return inFlightRequests.get();
    }

    /**
     * Executes a request handler with in-flight tracking.
     * Rejects new requests if graceful shutdown has already commenced.
     */
    public <T> T execute(Callable<T> handler) throws Exception {
        if (state.get() != LifecycleState.ACCEPTING_TRAFFIC) {
            throw new ServiceUnavailableException("Server is shutting down. Traffic refused (HTTP 503).");
        }

        inFlightRequests.incrementAndGet();
        try {
            // Re-check state in case shutdown commenced concurrently
            if (state.get() == LifecycleState.STOPPED) {
                throw new ServiceUnavailableException("Server has stopped.");
            }
            return handler.call();
        } finally {
            int remaining = inFlightRequests.decrementAndGet();
            if (state.get() == LifecycleState.DRAINING && remaining == 0) {
                synchronized (drainLock) {
                    drainLock.notifyAll();
                }
            }
        }
    }

    /**
     * Commences graceful shutdown, refusing new requests and waiting for in-flight requests to complete.
     *
     * @param timeout maximum time to wait for active requests to drain
     * @return true if drained cleanly within timeout; false if timed out (forced termination)
     */
    public boolean commenceShutdown(Duration timeout) throws InterruptedException {
        state.set(LifecycleState.DRAINING);

        long deadline = System.currentTimeMillis() + timeout.toMillis();
        synchronized (drainLock) {
            while (inFlightRequests.get() > 0) {
                long waitTime = deadline - System.currentTimeMillis();
                if (waitTime <= 0) {
                    state.set(LifecycleState.STOPPED);
                    return false; // Timed out waiting for active transactions
                }
                drainLock.wait(waitTime);
            }
        }

        state.set(LifecycleState.STOPPED);
        return true;
    }
}
