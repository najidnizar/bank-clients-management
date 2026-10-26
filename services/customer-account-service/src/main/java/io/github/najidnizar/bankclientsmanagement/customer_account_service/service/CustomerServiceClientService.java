package io.github.najidnizar.bankclientsmanagement.customer_account_service.service;

import feign.FeignException;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.client.CustomerServiceFeignClient;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.exception.CustomerNotFoundException;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.exception.CustomerServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
        }
    }

    private void customerServiceFallback(UUID customerId, Throwable throwable) {
        throw new CustomerServiceUnavailableException(customerId, throwable);
    }
}
