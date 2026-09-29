package za.co.molorane.javastartshere.ch04.exceptions;

/**
 * Demonstrates the physics of exceptions in the JVM.
 * <p>
 * Key Concepts (Chapter 4 Topic 2):
 * <ul>
 *   <li><b>Cost of fillInStackTrace():</b> Native JVM stack walking traverses up to 80 stack frames,
 *       resolving symbols and allocating {@link StackTraceElement} arrays on the heap.</li>
 *   <li><b>Stackless Exceptions:</b> In high-throughput pathways or pure control flow validations,
 *       overriding {@code fillInStackTrace()} eliminates the native stack walk, reducing instantiation
 *       cost by over 1,000x.</li>
 *   <li><b>Suppressed Exceptions:</b> In try-with-resources, if both the try body and the {@code close()}
 *       method throw exceptions, the close failure is preserved as a suppressed exception.</li>
 * </ul>
 */
public class ExceptionPhysics {

    /**
     * Standard exception: performs full native stack walk during instantiation.
     */
    public static class StandardBusinessException extends RuntimeException {
        public StandardBusinessException(String message) {
            super(message);
        }
    }

    /**
     * High-throughput stackless exception: skips native stack walk.
     */
    public static class StacklessValidationException extends RuntimeException {
        public StacklessValidationException(String message) {
            super(message);
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            // No-op: skips native JVM stack frame traversal
            return this;
        }
    }

    /**
     * Faulty resource used to demonstrate try-with-resources suppressed exceptions.
     */
    public static class FaultyResource implements AutoCloseable {
        private final boolean failOnClose;

        public FaultyResource(boolean failOnClose) {
            this.failOnClose = failOnClose;
        }

        public void performWork(boolean failWork) {
            if (failWork) {
                throw new IllegalStateException("Primary try-block operation failed");
            }
        }

        @Override
        public void close() {
            if (failOnClose) {
                throw new RuntimeException("Resource close cleanup failed");
            }
        }
    }

    /**
     * Executes work within try-with-resources to capture both primary and suppressed exceptions.
     */
    public static Throwable executeWithSuppression(boolean failWork, boolean failClose) {
        try (FaultyResource resource = new FaultyResource(failClose)) {
            resource.performWork(failWork);
            return null;
        } catch (Throwable t) {
            return t;
        }
    }
}
