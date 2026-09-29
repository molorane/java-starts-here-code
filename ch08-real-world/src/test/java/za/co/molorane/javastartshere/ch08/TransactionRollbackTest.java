package za.co.molorane.javastartshere.ch08;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.molorane.javastartshere.ch08.transaction.TransactionRollbackTrap.InsufficientInventoryException;
import za.co.molorane.javastartshere.ch08.transaction.TransactionRollbackTrap.PaymentGatewayCrashException;
import za.co.molorane.javastartshere.ch08.transaction.TransactionRollbackTrap.TransactionSimulator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Chapter 8: Transaction Boundaries & Rollback Traps Tests")
class TransactionRollbackTest {

    @Test
    @DisplayName("Default Spring @Transactional COMMITS on checked exception, violating intuitive expectations!")
    void defaultRuleCommitsOnCheckedException() {
        TransactionSimulator simulator = new TransactionSimulator();

        assertThatThrownBy(() -> simulator.executeWithDefaultSpringRule(true, false))
                .isInstanceOf(InsufficientInventoryException.class);

        // Surprising Spring default: Checked exceptions commit!
        assertThat(simulator.isCommitted()).isTrue();
        assertThat(simulator.isRolledBack()).isFalse();
        assertThat(simulator.getLedger()).containsExactly("ORDER_PLACED");
    }

    @Test
    @DisplayName("Default Spring @Transactional ROLLS BACK on unchecked RuntimeException")
    void defaultRuleRollsBackOnRuntimeException() {
        TransactionSimulator simulator = new TransactionSimulator();

        assertThatThrownBy(() -> simulator.executeWithDefaultSpringRule(false, true))
                .isInstanceOf(PaymentGatewayCrashException.class);

        // Unchecked exceptions trigger rollback
        assertThat(simulator.isRolledBack()).isTrue();
        assertThat(simulator.isCommitted()).isFalse();
    }

    @Test
    @DisplayName("Explicit rollbackFor = Exception.class ROLLS BACK on checked exceptions as well")
    void explicitRuleRollsBackOnCheckedException() {
        TransactionSimulator simulator = new TransactionSimulator();

        assertThatThrownBy(() -> simulator.executeWithExplicitRollbackRule(true))
                .isInstanceOf(InsufficientInventoryException.class);

        assertThat(simulator.isRolledBack()).isTrue();
        assertThat(simulator.isCommitted()).isFalse();
    }
}
