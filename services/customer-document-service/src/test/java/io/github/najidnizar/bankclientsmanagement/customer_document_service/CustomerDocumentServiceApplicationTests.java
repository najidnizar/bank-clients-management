package io.github.najidnizar.bankclientsmanagement.customer_document_service;

import io.github.najidnizar.bankclientsmanagement.customer_document_service.config.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Starts the full application context against a real PostgreSQL (Testcontainers); Eureka is disabled in tests
@SpringBootTest(properties = {
		"eureka.client.enabled=false",
		"minio.url=http://localhost:9000",
		"minio.access-key=test",
		"minio.secret-key=test"
})
class CustomerDocumentServiceApplicationTests extends AbstractPostgresIntegrationTest {

	@Test
	void contextLoads() {
	}

}
