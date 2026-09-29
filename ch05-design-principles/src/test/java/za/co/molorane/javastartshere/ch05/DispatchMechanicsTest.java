package za.co.molorane.javastartshere.ch05;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.molorane.javastartshere.ch05.dispatch.DispatchMechanics;
import za.co.molorane.javastartshere.ch05.dispatch.DispatchMechanics.DispatchResult;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Chapter 5: Polymorphism, Virtual Dispatch & Field Shadowing Tests")
class DispatchMechanicsTest {

    @Test
    @DisplayName("Fields are NOT polymorphic (compile-time bound), whereas methods dispatch virtually at runtime")
    void shouldDemonstrateFieldShadowingVsVirtualDispatch() {
        DispatchResult result = DispatchMechanics.evaluateDispatch();

        // 1. Fields are statically bound to the declared reference type:
        assertThat(result.fieldViaParentRef()).isEqualTo("STANDARD");
        assertThat(result.fieldViaChildRef()).isEqualTo("PREMIUM");

        // 2. Methods are dynamically dispatched to the runtime instance:
        assertThat(result.methodViaParentRef()).isEqualTo("PREMIUM_METHOD");
        assertThat(result.methodViaChildRef()).isEqualTo("PREMIUM_METHOD");

        // 3. Static methods are hidden and bound at compile time:
        assertThat(result.staticViaParentRef()).isEqualTo("ACCOUNT_STATIC");
        assertThat(result.staticViaChildRef()).isEqualTo("PREMIUM_STATIC");
    }
}
