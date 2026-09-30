package za.co.molorane.javastartshere.ch11;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

/**
 * Demonstrates Fail-Fast vs Fail-Safe / Weakly Consistent iterators:
 * - Fail-Fast iterators (ArrayList, HashSet) track modCount and throw {@link ConcurrentModificationException}.
 * - Safe iteration via {@link Iterator#remove()} and {@link List#removeIf(Predicate)}.
 * - Fail-Safe iterators (e.g., {@link CopyOnWriteArrayList}) operate over snapshots without throwing CME.
 */
public class IteratorPhysics {

    /**
     * Unsafe: triggers ConcurrentModificationException on standard ArrayList.
     */
    public static void unsafeRemove(List<String> list, String target) {
        for (String item : list) {
            if (item.equals(target)) {
                list.remove(item); // Modifies list directly while iterator is active!
            }
        }
    }

    /**
     * Safe approach 1: using explicit Iterator.remove().
     */
    public static void safeIteratorRemove(List<String> list, String target) {
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equals(target)) {
                iterator.remove(); // Safely updates expectedModCount inside the iterator
            }
        }
    }

    /**
     * Safe approach 2: Java 8+ removeIf predicate.
     */
    public static void safePredicateRemove(List<String> list, String target) {
        list.removeIf(item -> item.equals(target));
    }
}
