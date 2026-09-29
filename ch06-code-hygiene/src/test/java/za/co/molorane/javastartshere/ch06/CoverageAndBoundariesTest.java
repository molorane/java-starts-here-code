package za.co.molorane.javastartshere.ch06;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.molorane.javastartshere.ch06.coverage.InvoiceCalculator;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Chapter 6: Boundary Coverage & Defect Detection Tests")
class CoverageAndBoundariesTest {

    @Test
    @DisplayName("Calculates total with standard tax rate and rounding")
    void shouldCalculateStandardTaxTotal() {
        BigDecimal subtotal = new BigDecimal("100.00");
        BigDecimal taxRate = new BigDecimal("0.15"); // 15% VAT

        BigDecimal total = InvoiceCalculator.calculateTotal(subtotal, taxRate, false);

        assertThat(total).isEqualByComparingTo("115.00");
    }

    @Test
    @DisplayName("Exempt invoice returns subtotal without adding tax")
    void shouldReturnSubtotalForTaxExemptOrders() {
        BigDecimal subtotal = new BigDecimal("250.00");
        BigDecimal taxRate = new BigDecimal("0.15");

        BigDecimal total = InvoiceCalculator.calculateTotal(subtotal, taxRate, true);

        assertThat(total).isEqualByComparingTo("250.00");
    }

    @Test
    @DisplayName("Rejects negative subtotal or null parameters at boundaries")
    void shouldRejectInvalidBoundaryInputs() {
        BigDecimal taxRate = new BigDecimal("0.15");

        assertThatThrownBy(() -> InvoiceCalculator.calculateTotal(new BigDecimal("-1.00"), taxRate, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Subtotal must be positive");

        assertThatThrownBy(() -> InvoiceCalculator.calculateTotal(null, taxRate, false))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> InvoiceCalculator.calculateTotal(new BigDecimal("100.00"), null, false))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
