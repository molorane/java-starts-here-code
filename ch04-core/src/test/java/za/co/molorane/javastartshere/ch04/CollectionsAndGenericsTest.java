package za.co.molorane.javastartshere.ch04;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.molorane.javastartshere.ch04.collections.ModernCollections;
import za.co.molorane.javastartshere.ch04.collections.ModernCollections.MutableAccountKey;
import za.co.molorane.javastartshere.ch04.generics.PecsGenericsPipeline;
import za.co.molorane.javastartshere.ch04.generics.PecsGenericsPipeline.PriorityTransaction;
import za.co.molorane.javastartshere.ch04.generics.PecsGenericsPipeline.Transaction;
import za.co.molorane.javastartshere.ch04.generics.PecsGenericsPipeline.TypeReference;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Chapter 4: Modern Collections, Sequenced Collections & PECS Generics Tests")
class CollectionsAndGenericsTest {

    @Test
    @DisplayName("Collections.unmodifiableList reflects subsequent mutations to backing list, unlike List.of()")
    void shouldDemonstrateShallowUnmodifiableVsImmutable() {
        List<String> mutableBacking = new ArrayList<>(List.of("ALPHA", "BETA"));
        boolean viewWasMutated = ModernCollections.doesBackingListAffectUnmodifiableView(mutableBacking);

        assertThat(viewWasMutated).isTrue();

        // List.of() is truly immutable
        List<String> immutableList = List.of("ONE", "TWO");
        assertThatThrownBy(() -> immutableList.add("THREE"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Sequenced Collections (Java 21+) provide first, last, and reversed access")
    void shouldVerifySequencedCollectionOperations() {
        List<String> input = List.of("A", "B", "C");
        List<String> result = ModernCollections.inspectSequencedCollection(input);

        assertThat(result.get(0)).isEqualTo("A");           // getFirst()
        assertThat(result.get(1)).isEqualTo("C");           // getLast()
        assertThat(result.get(2)).isEqualTo("C,B,A");       // reversed()
    }

    @Test
    @DisplayName("Mutating a key after inserting into HashMap corrupts subsequent lookups")
    void shouldDemonstrateMutableKeyTrap() {
        Map<MutableAccountKey, String> map = new HashMap<>();
        MutableAccountKey key = new MutableAccountKey("ACC-1001");
        map.put(key, "VIP_CUSTOMER");

        // Initial lookup succeeds
        assertThat(map.get(key)).isEqualTo("VIP_CUSTOMER");

        // Mutate key: changes hashCode!
        key.setAccountNumber("ACC-MUTATED-9999");

        // Lookup fails because hashCode bucket changed
        assertThat(map.get(key)).isNull();
    }

    @Test
    @DisplayName("PECS: copy reads from producer (? extends T) and writes to consumer (? super T)")
    void shouldExecutePecsCopyPipeline() {
        List<PriorityTransaction> source = List.of(
                new PriorityTransaction(100.0),
                new PriorityTransaction(250.0)
        );

        List<Transaction> destination = new ArrayList<>();
        PecsGenericsPipeline.copy(source, destination);

        assertThat(destination)
                .hasSize(2)
                .extracting(Transaction::getAmount)
                .containsExactly(100.0, 250.0);
    }

    @Test
    @DisplayName("Super Type Token captures generic type arguments at runtime")
    void shouldCaptureGenericTypeViaSuperTypeToken() {
        var token = new TypeReference<List<String>>() {};
        assertThat(token.getType().getTypeName())
                .isEqualTo("java.util.List<java.lang.String>");
    }
}
