package za.co.molorane.javastartshere.ch05;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Chapter 5: Design Principles Tests")
class DesignPrinciplesTest {

    @Test
    @DisplayName("Should demonstrate the Overload Resolution Ladder: Widening beats Boxing")
    void shouldDemonstrateOverloadLadderWidening() {
        int primitiveInt = 42;
        // int widens to long (Phase 1) rather than boxing to Integer (Phase 2)
        String result = OverloadingLadder.resolve(primitiveInt);
        assertThat(result).isEqualTo("Phase 1: Primitive Widening (long)");
    }

    @Test
    @DisplayName("Should protect domain invariants against negative deposits or overdrafts")
    void shouldDefendDomainInvariants() {
        BankAccount account = new BankAccount("ACC-12345", BigDecimal.valueOf(100));

        account.deposit(BigDecimal.valueOf(50));
        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(150));

        assertThatThrownBy(() -> account.withdraw(BigDecimal.valueOf(200)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient funds");
    }
}
