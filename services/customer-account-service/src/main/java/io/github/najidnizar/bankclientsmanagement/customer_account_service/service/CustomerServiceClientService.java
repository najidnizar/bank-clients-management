package io.github.najidnizar.bankclientsmanagement.customer_account_service.service;

import feign.FeignException;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.client.CustomerServiceFeignClient;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.exception.CustomerNotFoundException;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.exception.CustomerServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Validates customers against the onboarding service, protected by the "customer-service" circuit breaker.
 * <p>
 * Only technical failures (5xx, timeouts, connection errors) mean that the onboarding service is unavailable:
 * they count as circuit breaker failures and end in {@link CustomerServiceUnavailableException} (503).
 * Client errors (4xx) are answers, not outages: they are ignored by the circuit breaker
 * (see {@code ignore-exceptions} in application.yml) and rethrown unchanged by their dedicated fallbacks.
 */
@Service
@RequiredArgsConstructor
public class CustomerServiceClientService {

    private final CustomerServiceFeignClient customerServiceFeignClient;

    @CircuitBreaker(name = "customer-service", fallbackMethod = "customerServiceFallback")
    public void validateCustomerExists(UUID customerId) {
        try {
            customerServiceFeignClient.getCustomer(customerId);
        } catch (FeignException.NotFound e) {
            throw new CustomerNotFoundException(customerId);
        } catch (FeignException.Forbidden e) {
            // The user (whose token is relayed) is not allowed to read this customer
            throw new AccessDeniedException("Not allowed to read customer " + customerId, e);
        }
    }

    // Resilience4j calls the fallback for every exception and picks the one whose exception type is the most specific.

    private void customerServiceFallback(UUID customerId, CustomerNotFoundException e) {
        throw e;
    }

    private void customerServiceFallback(UUID customerId, AccessDeniedException e) {
        throw e;
    }

    /** Other 4xx (400, 401...) reveal a bug in this service (bad request, token not relayed): kept as errors, not outages. */
    private void customerServiceFallback(UUID customerId, FeignException.FeignClientException e) {
        throw e;
    }

    /** 5xx, timeout, connection refused, or circuit open (CallNotPermittedException). */
    private void customerServiceFallback(UUID customerId, Throwable throwable) {
        throw new CustomerServiceUnavailableException(customerId, throwable);
    }
}
