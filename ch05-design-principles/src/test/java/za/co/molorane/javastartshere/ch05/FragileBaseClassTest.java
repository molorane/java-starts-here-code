package za.co.molorane.javastartshere.ch05;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.molorane.javastartshere.ch05.composition.FragileBatch.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Chapter 5: Fragile Base Class Problem vs Composition Tests")
class FragileBaseClassTest {

    @Test
    @DisplayName("Inheritance causes double-counting when parent addAll() delegates internally to add()")
    void shouldDemonstrateDoubleCountingTrapInInheritance() {
        var fragileBatch = new InstrumentedTransactionBatch<String>();
        fragileBatch.addAll(List.of("TX-1", "TX-2", "TX-3"));

        // Actual size is 3 items
        assertThat(fragileBatch.size()).isEqualTo(3);

        // But count was incremented in addAll() (+3) AND in overridden add() (+3) = 6!
        assertThat(fragileBatch.getAddCount()).isEqualTo(6);
    }

    @Test
    @DisplayName("Composition using forwarding decorator tracks exact count without double-counting")
    void shouldDemonstrateCleanCountWithComposition() {
        var baseBatch = new TransactionBatch<String>();
        var resilientBatch = new ResilientAuditedBatch<>(baseBatch);

        resilientBatch.addAll(List.of("TX-1", "TX-2", "TX-3"));

        assertThat(resilientBatch.size()).isEqualTo(3);
        // Correct count: exactly 3 items recorded!
        assertThat(resilientBatch.getAddCount()).isEqualTo(3);
    }
}
