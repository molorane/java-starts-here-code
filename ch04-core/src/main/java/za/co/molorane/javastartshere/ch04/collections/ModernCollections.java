package za.co.molorane.javastartshere.ch04.collections;

import java.util.*;

/**
 * Demonstrates Modern Collections mechanics and traps in modern Java.
 * <p>
 * Key Concepts (Chapter 4 Topic 3):
 * <ul>
 *   <li><b>List.of() vs Collections.unmodifiableList():</b> {@code List.of()} creates a truly immutable,
 *       space-efficient collection that prohibits nulls. In contrast, {@code unmodifiableList()} is merely
 *       a shallow view whose contents still change if the underlying source list is modified.</li>
 *   <li><b>Sequenced Collections (JEP 431, Java 21+):</b> Unified ordering contract via {@link SequencedCollection},
 *       providing {@code getFirst()}, {@code getLast()}, and {@code reversed()} views across Lists and Deques.</li>
 *   <li><b>The Mutable Key Trap:</b> Mutating an object after storing it as a {@link HashMap} key causes its
 *       {@code hashCode()} to change. As a result, subsequent lookups land in a different bucket and fail to find the entry.</li>
 * </ul>
 */
public class ModernCollections {

    /**
     * Mutable key illustrating the HashMap corruption trap.
     */
    public static class MutableAccountKey {
        private String accountNumber;

        public MutableAccountKey(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MutableAccountKey that = (MutableAccountKey) o;
            return Objects.equals(accountNumber, that.accountNumber);
        }

        @Override
        public int hashCode() {
            return Objects.hash(accountNumber);
        }
    }

    /**
     * Demonstrates shallow unmodifiable view vs true immutable list.
     */
    public static boolean doesBackingListAffectUnmodifiableView(List<String> mutableBacking) {
        List<String> view = Collections.unmodifiableList(mutableBacking);
        mutableBacking.add("MODIFIED_AFTER_VIEW");
        return view.contains("MODIFIED_AFTER_VIEW"); // true: view reflected underlying mutation!
    }

    /**
     * Demonstrates Sequenced Collections (Java 21+).
     */
    public static List<String> inspectSequencedCollection(List<String> input) {
        SequencedCollection<String> sequenced = new ArrayList<>(input);
        return List.of(
                sequenced.getFirst(),
                sequenced.getLast(),
                String.join(",", sequenced.reversed())
        );
    }
}
