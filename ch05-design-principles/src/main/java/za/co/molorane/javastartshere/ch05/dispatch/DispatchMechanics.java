package za.co.molorane.javastartshere.ch05.dispatch;

/**
 * Demonstrates Dynamic Virtual Dispatch vs Static Binding and Field Shadowing.
 * <p>
 * Key Concepts (Chapter 5 Topic 4):
 * <ul>
 *   <li><b>Virtual Method Dispatch:</b> Instance method calls in Java use {@code invokevirtual} or
 *       {@code invokeinterface}, dispatching dynamically at runtime based on the actual object instance on the heap.</li>
 *   <li><b>Fields Are NOT Polymorphic:</b> Field access is bound at compile time based strictly on the
 *       declared reference type. Subclass fields with identical names shadow, rather than override, superclass fields.</li>
 *   <li><b>Static Method Hiding:</b> Static methods cannot be overridden; they are resolved at compile time
 *       via {@code invokestatic} based on the reference type.</li>
 * </ul>
 */
public class DispatchMechanics {

    public static class Account {
        // Field to demonstrate shadowing
        public String tier = "STANDARD";

        public String getTier() {
            return "STANDARD_METHOD";
        }

        public static String identifyRole() {
            return "ACCOUNT_STATIC";
        }
    }

    public static class PremiumAccount extends Account {
        // Shadows Account.tier
        public String tier = "PREMIUM";

        @Override
        public String getTier() {
            return "PREMIUM_METHOD";
        }

        // Hides Account.identifyRole()
        public static String identifyRole() {
            return "PREMIUM_STATIC";
        }
    }

    public record DispatchResult(
            String fieldViaParentRef,
            String fieldViaChildRef,
            String methodViaParentRef,
            String methodViaChildRef,
            String staticViaParentRef,
            String staticViaChildRef
    ) {}

    public static DispatchResult evaluateDispatch() {
        PremiumAccount actualInstance = new PremiumAccount();
        Account parentRef = actualInstance;

        return new DispatchResult(
                parentRef.tier,               // "STANDARD": Field bound to reference type!
                actualInstance.tier,          // "PREMIUM": Field bound to reference type!
                parentRef.getTier(),          // "PREMIUM_METHOD": Dynamic virtual dispatch!
                actualInstance.getTier(),     // "PREMIUM_METHOD": Dynamic virtual dispatch!
                Account.identifyRole(),       // "ACCOUNT_STATIC": Static binding
                PremiumAccount.identifyRole() // "PREMIUM_STATIC": Static binding
        );
    }
}
