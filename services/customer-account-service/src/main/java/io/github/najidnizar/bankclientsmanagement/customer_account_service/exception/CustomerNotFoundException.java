package io.github.najidnizar.bankclientsmanagement.customer_account_service.exception;

import java.util.UUID;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(UUID customerId) {
        super("Customer not found: " + customerId);
    }
}
