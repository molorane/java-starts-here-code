package za.co.molorane.javastartshere.ch06;

/**
 * Demonstrates reducing cyclomatic complexity:
 * Refactoring deeply nested if/else statements into clean, testable strategies.
 */
public class ComplexityRefactorer {

    public enum CustomerTier {
        STANDARD, SILVER, GOLD, PLATINUM
    }

    public static double calculateDiscount(CustomerTier tier, double amount) {
        if (amount <= 0) {
            return 0.0;
        }
        return switch (tier) {
            case STANDARD -> 0.0;
            case SILVER -> amount * 0.05;
            case GOLD -> amount * 0.10;
            case PLATINUM -> amount * 0.20;
        };
    }
}
