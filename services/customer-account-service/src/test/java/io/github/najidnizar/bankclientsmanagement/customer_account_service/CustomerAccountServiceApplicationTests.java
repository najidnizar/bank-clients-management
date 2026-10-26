package io.github.najidnizar.bankclientsmanagement.customer_account_service;

import io.github.najidnizar.bankclientsmanagement.customer_account_service.config.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Starts the full application context against a real PostgreSQL (Testcontainers); Eureka is disabled in tests
@SpringBootTest(properties = {
		"eureka.client.enabled=false"
})
class CustomerAccountServiceApplicationTests extends AbstractPostgresIntegrationTest {

	@Test
	void contextLoads() {
	}

}
