package za.co.molorane.javastartshere.ch05.types;

import java.math.BigDecimal;

/**
 * Demonstrates how the Java compiler evaluates types and the {@code instanceof} operator.
 * <p>
 * Key Concepts (Chapter 5 Topic 2):
 * <ul>
 *   <li><b>Scenario 1 (Unrelated Classes):</b> Single class inheritance guarantees that two unrelated classes
 *       on separate branches can never share an instance. The compiler rejects this as inconvertible types.</li>
 *   <li><b>Scenario 2 (Class vs Interface):</b> Because open subclasses can implement arbitrary interfaces at runtime,
 *       testing a non-final class against an unimplemented interface compiles cleanly and is evaluated by the JVM.</li>
 *   <li><b>Pattern Matching for Switch (Java 21+):</b> Pattern matching over sealed hierarchies guarantees
 *       compile-time exhaustiveness without requiring a fallback {@code default} branch.</li>
 * </ul>
 */
public class CompilerTypeAnalyzer {

    public interface Refundable {
        BigDecimal refund();
    }

    public static class Payment {
        private final String id;
        private final BigDecimal amount;

        public Payment(String id, BigDecimal amount) {
            this.id = id;
            this.amount = amount;
        }

        public String getId() { return id; }
        public BigDecimal getAmount() { return amount; }
    }

    /**
     * Subclass that implements the interface that Payment did not.
     */
    public static class CreditCardPayment extends Payment implements Refundable {
        public CreditCardPayment(String id, BigDecimal amount) {
            super(id, amount);
        }

        @Override
        public BigDecimal refund() {
            return getAmount();
        }
    }

    /**
     * Demonstrates Scenario 2: Payment reference evaluated against Refundable interface.
     */
    public static boolean isRefundablePayment(Payment payment) {
        // Compiles cleanly even though static type Payment does NOT implement Refundable!
        return payment instanceof Refundable;
    }

    /**
     * Sealed hierarchy demonstrating exhaustive pattern matching switch (Java 21+).
     */
    public sealed interface PaymentMethod permits Card, InstantEft, CryptoWallet {}

    public record Card(String pan, String expiry) implements PaymentMethod {}
    public record InstantEft(String bankCode, String account) implements PaymentMethod {}
    public record CryptoWallet(String address) implements PaymentMethod {}

    /**
     * Exhaustive switch: No default branch needed because the compiler verifies all permitted types.
     */
    public static String resolvePaymentChannel(PaymentMethod method) {
        return switch (method) {
            case Card c -> "CARD:" + c.pan();
            case InstantEft eft -> "EFT:" + eft.bankCode();
            case CryptoWallet crypto -> "CRYPTO:" + crypto.address();
        };
    }
}
