package za.co.molorane.javastartshere.ch07.collections;

import java.util.Arrays;
import java.util.List;

/**
 * Demonstrates Java collection gotchas, fixed-size array wrappers, and array covariance traps.
 * <p>
 * Key Concepts (Chapter 7 Topics 1, 3, 7):
 * <ul>
 *   <li><b>Arrays.asList() Trap:</b> Returns a fixed-size list backed by the original array. Mutating
 *       via {@code set()} mutates the underlying array. Attempting {@code add()} or {@code remove()} throws
 *       {@link UnsupportedOperationException}.</li>
 *   <li><b>Array Covariance Trap:</b> Arrays in Java are covariant ({@code String[]} is an {@code Object[]}).
 *       Assigning an incompatible element at runtime passes compilation but explodes with {@link ArrayStoreException}.</li>
 *   <li><b>List.remove(int) vs List.remove(Object):</b> In {@code List<Integer>}, passing a primitive int
 *       removes by index, not value. Explicit boxing is required to remove by object equality.</li>
 * </ul>
 */
public class CollectionGotchas {

    public record AsListMutationResult(String[] backingArray, List<String> list) {}

    public static AsListMutationResult mutateArraysAsList(String[] original, int index, String newValue) {
        List<String> list = Arrays.asList(original);
        list.set(index, newValue); // Mutates backing array directly!
        return new AsListMutationResult(original, list);
    }

    /**
     * Demonstrates array covariance throwing ArrayStoreException.
     */
    public static void triggerArrayStoreException() {
        Object[] stringArray = new String[2];
        // Compiles cleanly because Object[] can reference String[]
        // But the JVM array store check fails at runtime!
        stringArray[0] = Integer.valueOf(42);
    }
}
