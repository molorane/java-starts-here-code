package za.co.molorane.javastartshere.ch06;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.molorane.javastartshere.ch06.refactoring.ValueObjectRefactoring;
import za.co.molorane.javastartshere.ch06.refactoring.ValueObjectRefactoring.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Chapter 6: Primitive Obsession vs Domain Value Objects Tests")
class ValueObjectRefactoringTest {

    @Test
    @DisplayName("EmailAddress value object enforces valid format at construction time")
    void shouldValidateEmailFormat() {
        EmailAddress valid = new EmailAddress("developer@example.com");
        assertThat(valid.value()).isEqualTo("developer@example.com");

        assertThatThrownBy(() -> new EmailAddress("invalid-email-address"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid email format");
    }

    @Test
    @DisplayName("Money value object enforces positive values and consistent currency arithmetic")
    void shouldEnforceMoneyInvariantsAndOperations() {
        Money m1 = new Money(new BigDecimal("100.50"), "ZAR");
        Money m2 = new Money(new BigDecimal("49.50"), "ZAR");
        Money sum = m1.add(m2);

        assertThat(sum.amount()).isEqualByComparingTo("150.00");
        assertThat(sum.currency()).isEqualTo("ZAR");

        // Negative money rejected
        assertThatThrownBy(() -> new Money(new BigDecimal("-10.00"), "USD"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be negative");

        // Currency mismatch rejected
        Money usd = new Money(new BigDecimal("50.00"), "USD");
        assertThatThrownBy(() -> m1.add(usd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot add distinct currencies");
    }

    @Test
    @DisplayName("User registration service prevents parameter transposition via strong typing")
    void shouldRegisterUserWithStrongTypes() {
        EmailAddress email = new EmailAddress("mothusi@example.com");
        NationalId id = new NationalId("9001015000080");
        Money credit = new Money(new BigDecimal("5000.00"), "ZAR");

        UserProfile profile = ValueObjectRefactoring.registerUser(email, id, credit);

        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.nationalId()).isEqualTo(id);
        assertThat(profile.creditLimit()).isEqualTo(credit);
    }
}
