package za.co.molorane.javastartshere.ch05;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.molorane.javastartshere.ch05.types.CompilerTypeAnalyzer;
import za.co.molorane.javastartshere.ch05.types.CompilerTypeAnalyzer.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Chapter 5: Thinking Like a Compiler - Type Analysis & Pattern Matching Tests")
class CompilerTypeAnalysisTest {

    @Test
    @DisplayName("Open subtyping: Non-final class evaluated against interface defers evaluation to runtime")
    void shouldEvaluateOpenSubtypingCorrectly() {
        Payment standardPayment = new Payment("PAY-1", new BigDecimal("50.00"));
        Payment cardPayment = new CreditCardPayment("PAY-2", new BigDecimal("120.00"));

        // standardPayment does not implement Refundable -> false
        assertThat(CompilerTypeAnalyzer.isRefundablePayment(standardPayment)).isFalse();

        // cardPayment is an instance of CreditCardPayment which implements Refundable -> true!
        assertThat(CompilerTypeAnalyzer.isRefundablePayment(cardPayment)).isTrue();
    }

    @Test
    @DisplayName("Exhaustive pattern matching switch over sealed interface (Java 21+)")
    void shouldExecuteExhaustiveSealedSwitch() {
        PaymentMethod card = new Card("4111-XXXX-XXXX-1111", "12/28");
        PaymentMethod eft = new InstantEft("ABSA-ZA", "987654321");
        PaymentMethod crypto = new CryptoWallet("0x71C...B29");

        assertThat(CompilerTypeAnalyzer.resolvePaymentChannel(card)).startsWith("CARD:4111");
        assertThat(CompilerTypeAnalyzer.resolvePaymentChannel(eft)).startsWith("EFT:ABSA");
        assertThat(CompilerTypeAnalyzer.resolvePaymentChannel(crypto)).startsWith("CRYPTO:0x71C");
    }
}
