package za.co.molorane.javastartshere.ch04;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Chapter 4: The Core We Thought We Knew Tests")
class CoreTypesTest {

    @Test
    @DisplayName("Should demonstrate Covariant Return Types without casting")
    void shouldDemonstrateCovariantReturns() {
        CovariantProducer.DogShelter shelter = new CovariantProducer.DogShelter();
        // Direct assignment to Dog without explicit cast thanks to covariant return
        CovariantProducer.Dog dog = shelter.adopt();
        assertThat(dog.bark()).isEqualTo("Woof!");
        assertThat(dog.species()).isEqualTo("Canis lupus familiaris");
    }

    @Test
    @DisplayName("Should verify compiler synthetic bridge methods for covariant returns")
    void shouldVerifySyntheticBridgeMethod() {
        Method[] methods = CovariantProducer.DogShelter.class.getDeclaredMethods();
        boolean hasBridge = false;
        for (Method m : methods) {
            if (m.getName().equals("adopt") && m.isBridge()) {
                hasBridge = true;
                break;
            }
        }
        assertThat(hasBridge).as("JVM compiler generates a synthetic bridge method to preserve polymorphism").isTrue();
    }

    @Test
    @DisplayName("Should verify Visibility Promotion from protected to public")
    void shouldVerifyVisibilityPromotion() {
        VisibilityPromotion.EnterpriseService service = new VisibilityPromotion.EnterpriseService();
        assertThat(service.executeOperation()).isEqualTo("Promoted public enterprise operation executed");
    }
}
