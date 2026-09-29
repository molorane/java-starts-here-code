package za.co.molorane.javastartshere.ch04.annotations;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates annotation processing via Dynamic Proxies and the "Self-Invocation Trap".
 * <p>
 * Key Concepts (Chapter 4 Topic 4):
 * <ul>
 *   <li><b>Dynamic Proxies:</b> Enterprise frameworks (e.g. Spring, Hibernate) implement annotations
 *       like {@code @Transactional} or {@code @AuditLog} by wrapping target objects in dynamic proxies.</li>
 *   <li><b>The Self-Invocation Trap:</b> When a method inside a target class calls another annotated method
 *       on {@code this}, the invocation is internal to the instance and completely bypasses the proxy advice.</li>
 * </ul>
 */
public class AuditProxyEngine {

    public interface PaymentProcessor {
        void transfer(double amount);
        void sendNotification();
    }

    public static class DefaultPaymentProcessor implements PaymentProcessor {
        private final List<String> executionSteps = new ArrayList<>();

        @Override
        @AuditLog(action = "TRANSFER_FUNDS")
        public void transfer(double amount) {
            executionSteps.add("Executed transfer: " + amount);
            // Self-invocation: calls sendNotification on 'this', bypassing proxy!
            sendNotification();
        }

        @Override
        @AuditLog(action = "SEND_ALERT")
        public void sendNotification() {
            executionSteps.add("Sent transfer alert");
        }

        public List<String> getExecutionSteps() {
            return List.copyOf(executionSteps);
        }
    }

    public record ProxyAuditResult(List<String> auditLog, List<String> executionSteps) {}

    @SuppressWarnings("unchecked")
    public static PaymentProcessor createAuditedProxy(PaymentProcessor target, List<String> auditTrail) {
        InvocationHandler handler = (proxy, method, args) -> {
            Method targetMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
            AuditLog annotation = targetMethod.getAnnotation(AuditLog.class);

            if (annotation != null) {
                auditTrail.add("AUDIT: " + annotation.action());
            }

            return method.invoke(target, args);
        };

        return (PaymentProcessor) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                new Class<?>[]{PaymentProcessor.class},
                handler
        );
    }
}
