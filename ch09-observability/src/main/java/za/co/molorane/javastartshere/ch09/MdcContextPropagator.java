package za.co.molorane.javastartshere.ch09;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/**
 * Demonstrates MDC (Mapped Diagnostic Context) propagation:
 * - Attaching trace and correlation IDs to thread-local contexts for correlated structured logging.
 * - Propagating context across thread pool and virtual thread boundaries without memory leaks.
 */
public class MdcContextPropagator {

    private static final Logger log = LoggerFactory.getLogger(MdcContextPropagator.class);
    public static final String TRACE_ID_KEY = "traceId";
    public static final String CORRELATION_ID_KEY = "correlationId";

    public static <T> T runWithTrace(String traceId, Callable<T> task) throws Exception {
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }
        return runWithContext(Map.of(TRACE_ID_KEY, traceId), task);
    }

    public static <T> T runWithContext(Map<String, String> context, Callable<T> task) throws Exception {
        Map<String, String> previousContext = MDC.getCopyOfContextMap();
        if (context != null) {
            MDC.setContextMap(context);
        } else {
            MDC.clear();
        }

        try {
            log.debug("Executing task under MDC context: {}", context);
            return task.call();
        } finally {
            // Restore previous context or clear to prevent thread-pool context contamination
            if (previousContext != null) {
                MDC.setContextMap(previousContext);
            } else {
                MDC.clear();
            }
        }
    }

    /**
     * Wraps a {@link Runnable} so that the caller's MDC context is captured at submission time
     * and transferred to the executing thread, safely cleaning up afterward.
     */
    public static Runnable wrap(Runnable task) {
        Map<String, String> capturedContext = MDC.getCopyOfContextMap();
        return () -> {
            Map<String, String> previousContext = MDC.getCopyOfContextMap();
            if (capturedContext != null) {
                MDC.setContextMap(capturedContext);
            } else {
                MDC.clear();
            }
            try {
                task.run();
            } finally {
                if (previousContext != null) {
                    MDC.setContextMap(previousContext);
                } else {
                    MDC.clear();
                }
            }
        };
    }

    /**
     * Wraps a {@link Callable} so that the caller's MDC context is captured at submission time
     * and transferred to the executing thread, safely cleaning up afterward.
     */
    public static <T> Callable<T> wrap(Callable<T> task) {
        Map<String, String> capturedContext = MDC.getCopyOfContextMap();
        return () -> {
            Map<String, String> previousContext = MDC.getCopyOfContextMap();
            if (capturedContext != null) {
                MDC.setContextMap(capturedContext);
            } else {
                MDC.clear();
            }
            try {
                return task.call();
            } finally {
                if (previousContext != null) {
                    MDC.setContextMap(previousContext);
                } else {
                    MDC.clear();
                }
            }
        };
    }

    /**
     * An {@link Executor} decorator that propagates MDC context across thread pool boundaries.
     */
    public static class ContextPropagatingExecutor implements Executor {
        private final Executor delegate;

        public ContextPropagatingExecutor(Executor delegate) {
            this.delegate = delegate;
        }

        @Override
        public void execute(Runnable command) {
            delegate.execute(wrap(command));
        }
    }
}
