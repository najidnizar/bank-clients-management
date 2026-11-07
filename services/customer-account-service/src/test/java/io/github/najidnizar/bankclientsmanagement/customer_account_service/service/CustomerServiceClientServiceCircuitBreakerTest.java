package io.github.najidnizar.bankclientsmanagement.customer_account_service.service;

import feign.FeignException;
import feign.Request;
import feign.Response;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.client.CustomerServiceFeignClient;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.config.AbstractPostgresIntegrationTest;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.exception.CustomerNotFoundException;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.exception.CustomerServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Runs the real Resilience4j circuit breaker (Spring AOP proxy + application.yml configuration)
 * with a mocked Feign client: client errors must not open the circuit, server errors must.
 */
@SpringBootTest(properties = "eureka.client.enabled=false")
class CustomerServiceClientServiceCircuitBreakerTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private CustomerServiceClientService customerServiceClientService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @MockitoBean
    private CustomerServiceFeignClient customerServiceFeignClient;

    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void resetCircuitBreaker() {
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("customer-service");
        circuitBreaker.reset();
    }

    @Test
    void unknownCustomerIsReportedAsNotFoundAndDoesNotOpenTheCircuit() {
        when(customerServiceFeignClient.getCustomer(any())).thenThrow(feignException(404));

        for (int i = 0; i < 10; i++) {
            assertThatThrownBy(() -> customerServiceClientService.validateCustomerExists(UUID.randomUUID()))
                    .isInstanceOf(CustomerNotFoundException.class);
        }

        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    void forbiddenIsReportedAsAccessDeniedAndDoesNotOpenTheCircuit() {
        when(customerServiceFeignClient.getCustomer(any())).thenThrow(feignException(403));

        for (int i = 0; i < 10; i++) {
            assertThatThrownBy(() -> customerServiceClientService.validateCustomerExists(UUID.randomUUID()))
                    .isInstanceOf(AccessDeniedException.class);
        }

        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    void serverErrorsAreReportedAsUnavailableAndOpenTheCircuit() {
        when(customerServiceFeignClient.getCustomer(any())).thenThrow(feignException(500));

        // minimum-number-of-calls = 5, failure-rate-threshold = 50 %
        for (int i = 0; i < 5; i++) {
            assertThatThrownBy(() -> customerServiceClientService.validateCustomerExists(UUID.randomUUID()))
                    .isInstanceOf(CustomerServiceUnavailableException.class);
        }

        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    private static FeignException feignException(int status) {
        Request request = Request.create(Request.HttpMethod.GET, "/api/v1/customers/id",
                Map.of(), null, StandardCharsets.UTF_8, null);
        Response response = Response.builder()
                .status(status)
                .reason("error")
                .request(request)
                .headers(Map.of())
                .build();
        return FeignException.errorStatus("getCustomer", response);
    }
}
