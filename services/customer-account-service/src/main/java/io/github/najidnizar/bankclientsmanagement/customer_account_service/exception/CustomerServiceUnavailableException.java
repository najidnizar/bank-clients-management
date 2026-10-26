package io.github.najidnizar.bankclientsmanagement.customer_account_service.exception;

import java.util.UUID;

public class CustomerServiceUnavailableException extends RuntimeException {

    public CustomerServiceUnavailableException(UUID customerId, Throwable cause) {
        super("Customer service is currently unavailable for customer " + customerId, cause);
    }
}
