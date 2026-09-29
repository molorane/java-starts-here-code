package za.co.molorane.javastartshere.ch09;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Demonstrates MDC (Mapped Diagnostic Context) propagation:
 * Attaching trace IDs to thread-local contexts for correlated structured logging.
 */
public class MdcContextPropagator {

    private static final Logger log = LoggerFactory.getLogger(MdcContextPropagator.class);
    public static final String TRACE_ID_KEY = "traceId";

    public static <T> T runWithTrace(String traceId, Callable<T> task) throws Exception {
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
        }
        MDC.put(TRACE_ID_KEY, traceId);
        try {
            log.info("Executing task under trace context");
            return task.call();
        } finally {
            MDC.remove(TRACE_ID_KEY);
        }
    }
}
