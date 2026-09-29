package za.co.molorane.javastartshere.ch08.transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates the Spring Transaction Boundary and Rollback Traps.
 * <p>
 * Key Concepts (Chapter 8 Topic 5):
 * <ul>
 *   <li><b>Checked vs Unchecked Rollback:</b> By default, Spring's {@code @Transactional} rolls back
 *       strictly on unchecked exceptions ({@link RuntimeException} and {@link Error}).
 *       If a checked {@link Exception} is thrown, the transaction is <i>committed</i> unless
 *       {@code rollbackFor = Exception.class} is explicitly declared!</li>
 *   <li><b>The Self-Invocation Trap:</b> In Spring AOP, calling a transactional method from within the
 *       same bean uses {@code this.method()}, bypassing the Spring CGLIB/JDK proxy and executing
 *       without transaction management.</li>
 * </ul>
 */
public class TransactionRollbackTrap {

    public static class InsufficientInventoryException extends Exception {
        public InsufficientInventoryException(String message) {
            super(message);
        }
    }

    public static class PaymentGatewayCrashException extends RuntimeException {
        public PaymentGatewayCrashException(String message) {
            super(message);
        }
    }

    /**
     * Simulated transaction state.
     */
    public static class TransactionSimulator {
        private final List<String> ledger = new ArrayList<>();
        private boolean committed = false;
        private boolean rolledBack = false;

        public void recordEntry(String entry) {
            ledger.add(entry);
        }

        /**
         * Simulates Spring's default transaction advice:
         * - Rolls back on RuntimeException or Error
         * - Commits on checked Exception!
         */
        public void executeWithDefaultSpringRule(boolean throwChecked, boolean throwUnchecked) throws Exception {
            try {
                recordEntry("ORDER_PLACED");
                if (throwChecked) {
                    throw new InsufficientInventoryException("Out of stock!");
                }
                if (throwUnchecked) {
                    throw new PaymentGatewayCrashException("Gateway crashed!");
                }
                committed = true;
            } catch (RuntimeException | Error e) {
                rolledBack = true;
                throw e;
            } catch (Exception e) {
                // Spring default: COMMITS despite checked exception!
                committed = true;
                throw e;
            }
        }

        /**
         * Simulates @Transactional(rollbackFor = Exception.class)
         */
        public void executeWithExplicitRollbackRule(boolean throwChecked) throws Exception {
            try {
                recordEntry("ORDER_PLACED");
                if (throwChecked) {
                    throw new InsufficientInventoryException("Out of stock!");
                }
                committed = true;
            } catch (Exception e) {
                rolledBack = true;
                throw e;
            }
        }

        public boolean isCommitted() { return committed; }
        public boolean isRolledBack() { return rolledBack; }
        public List<String> getLedger() { return List.copyOf(ledger); }
    }
}
