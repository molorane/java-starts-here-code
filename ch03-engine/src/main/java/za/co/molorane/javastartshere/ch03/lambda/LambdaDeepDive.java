package za.co.molorane.javastartshere.ch03.lambda;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Deep Dive into Java Lambda Expressions and Method References under the JVM hood.
 * <p>
 * Key Concepts Demonstrated:
 * <ul>
 *   <li><b>Stateless (Non-Capturing) Lambdas:</b> The JVM emits {@code invokedynamic} instructions
 *       linking via {@link java.lang.invoke.LambdaMetafactory}. For non-capturing lambdas, the runtime
 *       caches and returns a singleton instance (zero heap allocations per call).</li>
 *   <li><b>Stateful (Capturing) Lambdas:</b> When a lambda captures a local variable (which must be
 *       effectively final) or enclosing instance field, a new object instance is allocated on the heap.</li>
 *   <li><b>The 4 Method Reference Archetypes:</b>
 *       Static, Bound Instance, Unbound Instance, and Constructor reference.</li>
 * </ul>
 */
public class LambdaDeepDive {

    /**
     * Domain record representing an order candidate for processing.
     */
    public record OrderPayload(String orderId, String sku, double amount) {
        public static boolean isEligibleForDiscount(OrderPayload order) {
            return order != null && order.amount() >= 100.0;
        }

        public boolean hasValidSku() {
            return sku != null && sku.startsWith("ORD-");
        }
    }

    /**
     * Non-capturing (stateless) lambda.
     * <p>
     * Under the hood: The JVM extracts this lambda body into a synthetic static method
     * and uses LambdaMetafactory to return a constant singleton instance.
     */
    public static OrderValidator<String> createNonCapturingValidator() {
        return s -> s != null && !s.isBlank();
    }

    /**
     * Capturing lambda.
     * <p>
     * Under the hood: Because {@code minLength} is captured from the surrounding stack frame,
     * the JVM must allocate a new instance on the heap to store the captured parameter.
     *
     * @param minLength minimum acceptable string length (effectively final)
     */
    public static OrderValidator<String> createCapturingValidator(int minLength) {
        return s -> s != null && s.length() >= minLength;
    }

    /**
     * Archetype 1: Reference to a Static Method (ClassName::staticMethod).
     */
    public static Predicate<OrderPayload> staticMethodReference() {
        return OrderPayload::isEligibleForDiscount;
    }

    /**
     * Archetype 2: Reference to an Instance Method of an Arbitrary Object of a Particular Type (ContainingType::methodName).
     */
    public static Predicate<OrderPayload> unboundInstanceMethodReference() {
        return OrderPayload::hasValidSku;
    }

    /**
     * Archetype 3: Reference to an Instance Method of a Particular Object (instanceRef::methodName).
     *
     * @param validator specific instance whose method will be invoked
     */
    public static Predicate<String> boundInstanceMethodReference(OrderValidator<String> validator) {
        return validator::validate;
    }

    /**
     * Archetype 4: Reference to a Constructor (ClassName::new).
     */
    public static BiFunction<String, Double, OrderPayload> constructorReference() {
        return (orderId, amount) -> new OrderPayload(orderId, "ORD-DEFAULT", amount);
    }

    /**
     * Higher-order function: Combines and executes validation across a collection.
     */
    public static List<OrderPayload> filterOrders(List<OrderPayload> orders, Predicate<OrderPayload> predicate) {
        return orders.stream()
                .filter(predicate)
                .toList();
    }
}
