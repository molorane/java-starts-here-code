package za.co.molorane.javastartshere.ch04;

/**
 * Demonstrates Covariant Return Types (Java 5+) and Bridge Methods:
 * Subclasses can narrow the return type of an overridden method.
 */
public class CovariantProducer {

    public static class Animal {
        public String species() {
            return "Generic Animal";
        }
    }

    public static class Dog extends Animal {
        @Override
        public String species() {
            return "Canis lupus familiaris";
        }

        public String bark() {
            return "Woof!";
        }
    }

    public static class AnimalShelter {
        public Animal adopt() {
            return new Animal();
        }
    }

    public static class DogShelter extends AnimalShelter {
        // Covariant return type: returns Dog instead of Animal
        @Override
        public Dog adopt() {
            return new Dog();
        }
    }
}
