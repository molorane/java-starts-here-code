package za.co.molorane.javastartshere.ch04;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.molorane.javastartshere.ch04.annotations.AuditProxyEngine;
import za.co.molorane.javastartshere.ch04.annotations.AuditProxyEngine.DefaultPaymentProcessor;
import za.co.molorane.javastartshere.ch04.annotations.AuditProxyEngine.PaymentProcessor;
import za.co.molorane.javastartshere.ch04.reflection.ModernReflection;
import za.co.molorane.javastartshere.ch04.reflection.ModernReflection.UserProfile;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Chapter 4: Annotations, Dynamic Proxies & Modern Reflection Tests")
class AnnotationsAndReflectionTest {

    @Test
    @DisplayName("Dynamic Proxy intercepts annotated external call, but self-invocation bypasses proxy advice")
    void shouldDemonstrateDynamicProxyAndSelfInvocationTrap() {
        DefaultPaymentProcessor target = new DefaultPaymentProcessor();
        List<String> auditTrail = new ArrayList<>();
        PaymentProcessor proxy = AuditProxyEngine.createAuditedProxy(target, auditTrail);

        // Call transfer() on proxy:
        // 1. Proxy intercepts transfer() because it is annotated with @AuditLog
        // 2. Inside transfer(), target calls sendNotification() on 'this' (self-invocation)
        // 3. The internal sendNotification() bypasses proxy advice, so no second audit entry is made!
        proxy.transfer(500.0);

        assertThat(auditTrail)
                .hasSize(1)
                .containsExactly("AUDIT: TRANSFER_FUNDS");

        assertThat(target.getExecutionSteps())
                .containsExactly("Executed transfer: 500.0", "Sent transfer alert");
    }

    @Test
    @DisplayName("Record reflection extracts component names natively")
    void shouldExtractRecordComponentNames() {
        List<String> names = ModernReflection.extractRecordFieldNames(UserProfile.class);

        assertThat(names).containsExactly("username", "role", "active");
    }

    @Test
    @DisplayName("MethodHandle invokes method with high performance JIT optimization")
    void shouldInvokeViaMethodHandle() throws Throwable {
        UserProfile profile = new UserProfile("mothusi", "ADMIN", true);
        String username = ModernReflection.invokeViaMethodHandle(profile);

        assertThat(username).isEqualTo("mothusi");
    }
}
