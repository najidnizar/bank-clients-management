package io.github.najidnizar.bankclientsmanagement.customer_account_service.config;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Shared PostgreSQL container for integration tests.
 * Started once per JVM (singleton pattern) so that Spring's cached test contexts
 * never point to a container that was stopped after a previous test class.
 */
public abstract class AbstractPostgresIntegrationTest {

    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    static {
        postgres.start();
    }

}
