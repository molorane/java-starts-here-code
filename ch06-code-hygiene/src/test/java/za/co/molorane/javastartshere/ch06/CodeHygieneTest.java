package za.co.molorane.javastartshere.ch06;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Chapter 6: Code Hygiene Tests")
class CodeHygieneTest {

    @Test
    @DisplayName("Should verify linear complexity pattern switch evaluation")
    void shouldCalculateDiscountsAccurately() {
        assertThat(ComplexityRefactorer.calculateDiscount(ComplexityRefactorer.CustomerTier.STANDARD, 100.0)).isEqualTo(0.0);
        assertThat(ComplexityRefactorer.calculateDiscount(ComplexityRefactorer.CustomerTier.GOLD, 100.0)).isEqualTo(10.0);
        assertThat(ComplexityRefactorer.calculateDiscount(ComplexityRefactorer.CustomerTier.PLATINUM, 100.0)).isEqualTo(20.0);
    }
}
