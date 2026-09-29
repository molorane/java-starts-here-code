package za.co.molorane.javastartshere.ch05.composition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Demonstrates the Fragile Base Class problem and why Composition beats Inheritance.
 * <p>
 * Key Concepts (Chapter 5 Topic 6):
 * <ul>
 *   <li><b>White-Box Coupling:</b> Inheriting from a concrete class exposes the subclass to the parent's
 *       internal method coordination. Overriding both {@code add()} and {@code addAll()} when the parent's
 *       {@code addAll()} internally delegates to {@code add()} leads to silent double-counting bugs.</li>
 *   <li><b>Black-Box Composition:</b> Using a forwarding decorator (wrapper) delegating to an interface
 *       completely isolates metrics tracking from internal implementation quirks.</li>
 * </ul>
 */
public class FragileBatch {

    public interface Batch<T> {
        void add(T item);
        void addAll(Collection<? extends T> items);
        int size();
    }

    /**
     * Base implementation provided by a library or framework.
     */
    public static class TransactionBatch<T> implements Batch<T> {
        protected final List<T> items = new ArrayList<>();

        @Override
        public void add(T item) {
            items.add(item);
        }

        @Override
        public void addAll(Collection<? extends T> collection) {
            for (T item : collection) {
                add(item); // Self-call: delegates to add()!
            }
        }

        @Override
        public int size() {
            return items.size();
        }
    }

    /**
     * FRAGILE SUBCLASS: Demonstrates the double-counting bug.
     */
    public static class InstrumentedTransactionBatch<T> extends TransactionBatch<T> {
        private int addCount = 0;

        @Override
        public void add(T item) {
            addCount++;
            super.add(item);
        }

        @Override
        public void addAll(Collection<? extends T> collection) {
            addCount += collection.size(); // Incremented here...
            super.addAll(collection);      // ...and incremented again when super.addAll() calls add()!
        }

        public int getAddCount() {
            return addCount;
        }
    }

    /**
     * RESILIENT FORWARDING DECORATOR: Composition over inheritance.
     */
    public static class ResilientAuditedBatch<T> implements Batch<T> {
        private final Batch<T> delegate;
        private int addCount = 0;

        public ResilientAuditedBatch(Batch<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public void add(T item) {
            addCount++;
            delegate.add(item);
        }

        @Override
        public void addAll(Collection<? extends T> items) {
            addCount += items.size();
            delegate.addAll(items);
        }

        @Override
        public int size() {
            return delegate.size();
        }

        public int getAddCount() {
            return addCount;
        }
    }
}
