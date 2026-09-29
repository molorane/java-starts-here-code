package za.co.molorane.javastartshere.ch10;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Demonstrates the architectural contract between Kubernetes probes and Spring Boot Actuator:
 *
 * 1. Liveness Probe: Must ONLY verify internal JVM health (e.g., deadlocks, fatal memory corruption).
 *    Never check external dependencies (DB, Kafka) in Liveness probes to prevent cascading restart storms!
 *
 * 2. Readiness Probe: Verifies whether the instance is ready to accept user network traffic.
 *    Reflects cache warming, DB pool readiness, and graceful draining.
 */
public class EnterpriseHealthIndicator {

    public static class InternalLivenessIndicator implements HealthIndicator {
        private final AtomicBoolean jvmHealthy = new AtomicBoolean(true);

        public void markBroken() {
            jvmHealthy.set(false);
        }

        public void markHealthy() {
            jvmHealthy.set(true);
        }

        @Override
        public Health health() {
            if (!jvmHealthy.get()) {
                return Health.down()
                        .withDetail("reason", "Fatal internal JVM state detected")
                        .build();
            }
            return Health.up()
                    .withDetail("jvmStatus", "RUNNING")
                    .build();
        }
    }

    public static class TrafficReadinessIndicator implements HealthIndicator {
        private final AtomicBoolean acceptingTraffic = new AtomicBoolean(false);
        private final AtomicBoolean databaseConnected = new AtomicBoolean(false);

        public void setAcceptingTraffic(boolean accepting) {
            this.acceptingTraffic.set(accepting);
        }

        public void setDatabaseConnected(boolean connected) {
            this.databaseConnected.set(connected);
        }

        @Override
        public Health health() {
            if (!acceptingTraffic.get()) {
                return Health.down()
                        .withDetail("reason", "Refusing traffic during startup warmup or graceful draining")
                        .build();
            }
            if (!databaseConnected.get()) {
                return Health.down()
                        .withDetail("reason", "Downstream database connection pool not ready")
                        .build();
            }
            return Health.up()
                    .withDetail("readiness", "ACCEPTING_TRAFFIC")
                    .build();
        }
    }
}
