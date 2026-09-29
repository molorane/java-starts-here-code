package za.co.molorane.javastartshere.ch10;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Application entrypoint for Chapter 10: The Enterprise Toolkit.
 * Demonstrates Kubernetes readiness/liveness, Flyway migrations, and Actuator metrics.
 */
@SpringBootApplication
public class EnterpriseToolkitApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnterpriseToolkitApplication.class, args);
    }
}
