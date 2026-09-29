package za.co.molorane.javastartshere.ch11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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
    @DisplayName("Should sort employees using multi-field declarative Comparator chain")
    void testComparatorChaining() {
        Employee e1 = new Employee("Engineering", "Alice", 120_000);
        Employee e2 = new Employee("Engineering", "Bob", 150_000);
        Employee e3 = new Employee("Finance", "Charlie", 95_000);

        List<Employee> list = new ArrayList<>(List.of(e1, e2, e3));
        list.sort(Employee.COMPARATOR);

        // In Engineering, Bob has higher salary so Bob comes first, then Alice, then Finance Charlie
        assertThat(list).containsExactly(e2, e1, e3);
    }
}
