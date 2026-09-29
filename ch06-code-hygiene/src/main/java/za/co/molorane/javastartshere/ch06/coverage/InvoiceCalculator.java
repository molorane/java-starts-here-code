package za.co.molorane.javastartshere.ch06.coverage;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Demonstrates the Dangerous Illusions of Code Coverage.
 * <p>
 * Key Concepts (Chapter 6 Topic 6):
 * <ul>
 *   <li><b>Phantom Coverage:</b> Tests that execute lines without verifying assertions satisfy arbitrary
 *       coverage quotas (e.g. 90% line coverage) while providing zero actual defect-detection capability.</li>
 *   <li><b>Rigorous Boundary Coverage:</b> Real confidence comes from asserting boundary inputs (zero, negative,
 *       nulls, rounding edges, and boolean branches).</li>
 * </ul>
 */
public class InvoiceCalculator {

    public static BigDecimal calculateTotal(BigDecimal subtotal, BigDecimal taxRate, boolean isExempt) {
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Subtotal must be positive and non-null");
        }
        if (taxRate == null || taxRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Tax rate must be positive and non-null");
        }

        if (isExempt) {
            return subtotal.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal tax = subtotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        return subtotal.add(tax).setScale(2, RoundingMode.HALF_UP);
    }
}
