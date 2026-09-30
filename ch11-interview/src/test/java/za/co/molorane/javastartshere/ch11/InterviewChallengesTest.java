package za.co.molorane.javastartshere.ch11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Chapter 11: Java Interview Challenges Tests")
class InterviewChallengesTest {

    @Test
    @DisplayName("Should demonstrate HashMap collision handling with deliberate identical hashCodes")
    void testHashMapCollisions() {
        class CollidingKey {
            final String id;
            CollidingKey(String id) { this.id = id; }
            @Override public boolean equals(Object o) {
                return (o instanceof CollidingKey other) && id.equals(other.id);
            }
            @Override public int hashCode() { return 42; } // Deliberate 100% collision!
        }

        Map<CollidingKey, String> map = new HashMap<>();
        CollidingKey k1 = new CollidingKey("key-1");
        CollidingKey k2 = new CollidingKey("key-2");

        map.put(k1, "value-1");
        map.put(k2, "value-2");

        // Despite identical hash codes, HashMap resolves keys correctly via .equals()!
        assertThat(map.get(k1)).isEqualTo("value-1");
        assertThat(map.get(k2)).isEqualTo("value-2");
    }

    @Test
    @DisplayName("Should demonstrate HashMap bitwise index calculation and power-of-two requirement")
    void testHashMapBitwiseIndexCalculation() {
        int capacity = 16;
        int index = HashMapDeepMechanics.computeBucketIndex("test-key", capacity);
        assertThat(index).isBetween(0, capacity - 1);

        // Non-power-of-two capacity must be rejected
        assertThatThrownBy(() -> HashMapDeepMechanics.computeBucketIndex("key", 15))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Capacity must be a power of two");
    }

    @Test
    @DisplayName("Should demonstrate the Mutable Key Trap: mutating key after put causes get() to fail")
    void testMutableKeyTrap() {
        Map<HashMapDeepMechanics.MutableKey, String> map = new HashMap<>();
        HashMapDeepMechanics.MutableKey key = new HashMapDeepMechanics.MutableKey("ORD-101");

        map.put(key, "Active Order");
        assertThat(map.get(key)).isEqualTo("Active Order");

        // Now mutate the key's state
        key.setValue("ORD-999");

        // get() fails because the bucket index changed! The entry is silently lost in the old bucket.
        assertThat(map.get(key)).isNull();
    }

    @Test
    @DisplayName("Should demonstrate the catastrophic missing hashCode trap in HashSet")
    void testMissingHashCodeContractTrap() {
        // 1. Broken implementation: overrides equals() without hashCode()
        Set<EqualsHashCodeContract.BrokenUser> brokenSet = new HashSet<>();
        brokenSet.add(new EqualsHashCodeContract.BrokenUser("USR-1"));

        // Searching with a new instance with the same logical ID fails!
        boolean foundBroken = brokenSet.contains(new EqualsHashCodeContract.BrokenUser("USR-1"));
        assertThat(foundBroken).isFalse();

        // 2. Correct implementation: obeys the equals & hashCode contract
        Set<EqualsHashCodeContract.CorrectUser> correctSet = new HashSet<>();
        correctSet.add(new EqualsHashCodeContract.CorrectUser("USR-1"));

        boolean foundCorrect = correctSet.contains(new EqualsHashCodeContract.CorrectUser("USR-1"));
        assertThat(foundCorrect).isTrue();
    }

    @Test
    @DisplayName("Should verify the 5 formal mathematical properties of the equals contract")
    void testEqualsMathematicalContract() {
        EqualsHashCodeContract.CorrectUser x = new EqualsHashCodeContract.CorrectUser("A");
        EqualsHashCodeContract.CorrectUser y = new EqualsHashCodeContract.CorrectUser("A");
        EqualsHashCodeContract.CorrectUser z = new EqualsHashCodeContract.CorrectUser("A");

        // 1. Reflexive
        assertThat(x.equals(x)).isTrue();

        // 2. Symmetric
        assertThat(x.equals(y)).isEqualTo(y.equals(x));

        // 3. Transitive
        assertThat(x.equals(y) && y.equals(z)).isTrue();
        assertThat(x.equals(z)).isTrue();

        // 4. Consistent
        assertThat(x.equals(y)).isTrue();
        assertThat(x.equals(y)).isTrue();

        // 5. Non-nullity
        assertThat(x.equals(null)).isFalse();
    }

    @Test
    @DisplayName("Should demonstrate natural ordering via Comparable and multi-field declarative Comparator chaining")
    void testComparatorChaining() {
        Employee e1 = new Employee(101L, "Lerato", "Moloi", "Engineering", "Johannesburg", 120_000);
        Employee e2 = new Employee(102L, "Sipho", "Dlamini", "Finance", "Durban", 95_000);
        Employee e3 = new Employee(103L, "Lerato", "Khumalo", "Engineering", "Cape Town", 140_000);

        // 1. Natural ordering via Comparable in TreeSet (sorted by ID ascending: 101, 102, 103)
        TreeSet<Employee> naturallySorted = new TreeSet<>();
        naturallySorted.addAll(List.of(e3, e1, e2));
        assertThat(naturallySorted).containsExactly(e1, e2, e3);

        // 2. SQL-style multi-column sort:
        // ORDER BY department, lastName, firstName, salary DESC
        List<Employee> list = new ArrayList<>(List.of(e1, e2, e3));
        list.sort(Employee.SQL_STYLE_COMPARATOR);

        // Engineering comes before Finance; Khumalo comes before Moloi in Engineering
        assertThat(list).containsExactly(e3, e1, e2);
    }

    @Test
    @DisplayName("Should demonstrate high-throughput concurrent counters with AtomicLong and LongAdder")
    void testConcurrencyInterviewBasics() throws Exception {
        ConcurrencyInterviewBasics counters = new ConcurrencyInterviewBasics();
        int threadCount = 8;
        int incrementsPerThread = 10_000;

        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            pool.submit(() -> {
                try {
                    for (int j = 0; j < incrementsPerThread; j++) {
                        counters.incrementAtomic();
                        counters.incrementAdder();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        pool.shutdown();

        long expectedTotal = (long) threadCount * incrementsPerThread;
        assertThat(counters.getAtomicValue()).isEqualTo(expectedTotal);
        assertThat(counters.getAdderValue()).isEqualTo(expectedTotal);
    }

    @Test
    @DisplayName("Should demonstrate that Java is strictly Pass-by-Value")
    void testPassByValueMechanics() {
        PassByValuePhysics.Account acc1 = new PassByValuePhysics.Account(100.0);
        PassByValuePhysics.Account acc2 = new PassByValuePhysics.Account(200.0);

        // 1. Modifying object state via reference copy works, but rebinding parameter reference does not affect caller
        PassByValuePhysics.modifyAccount(acc1, 500.0, 9999.0);
        assertThat(acc1.getBalance()).isEqualTo(500.0); // State modified on heap

        // 2. Swapping references has zero effect on the caller's variables
        PassByValuePhysics.swap(acc1, acc2);
        assertThat(acc1.getBalance()).isEqualTo(500.0);
        assertThat(acc2.getBalance()).isEqualTo(200.0);
    }

    @Test
    @DisplayName("Should demonstrate Fail-Fast ConcurrentModificationException and safe alternatives")
    void testFailFastIteratorAndSafeRemoval() {
        // 1. Unsafe iteration throws ConcurrentModificationException
        List<String> unsafeList = new ArrayList<>(List.of("alpha", "beta", "gamma", "delta"));
        assertThatThrownBy(() -> IteratorPhysics.unsafeRemove(unsafeList, "beta"))
                .isInstanceOf(java.util.ConcurrentModificationException.class);

        // 2. Safe removal using Iterator.remove()
        List<String> safeIteratorList = new ArrayList<>(List.of("alpha", "beta", "gamma"));
        IteratorPhysics.safeIteratorRemove(safeIteratorList, "beta");
        assertThat(safeIteratorList).containsExactly("alpha", "gamma");

        // 3. Safe removal using Collection.removeIf()
        List<String> safePredicateList = new ArrayList<>(List.of("alpha", "beta", "gamma"));
        IteratorPhysics.safePredicateRemove(safePredicateList, "beta");
        assertThat(safePredicateList).containsExactly("alpha", "gamma");

        // 4. Fail-Safe iteration with CopyOnWriteArrayList allows concurrent mutation during loop
        List<String> cowList = new java.util.concurrent.CopyOnWriteArrayList<>(List.of("A", "B", "C"));
        for (String item : cowList) {
            if ("B".equals(item)) {
                cowList.add("D"); // No ConcurrentModificationException!
            }
        }
        assertThat(cowList).containsExactly("A", "B", "C", "D");
    }

    @Test
    @DisplayName("Should demonstrate volatile ordering/visibility and AtomicInteger atomicity")
    void testVolatileAtomicityGotcha() throws Exception {
        VolatileAtomicityGotcha gotcha = new VolatileAtomicityGotcha();
        int threads = 4;
        int perThread = 5_000;

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int t = 0; t < threads; t++) {
            pool.submit(() -> {
                try {
                    for (int i = 0; i < perThread; i++) {
                        gotcha.incrementVolatile();
                        gotcha.incrementAtomic();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        pool.shutdown();

        // AtomicInteger is guaranteed to reach exact sum without lost updates
        assertThat(gotcha.getAtomicCount()).isEqualTo(threads * perThread);
    }
}
