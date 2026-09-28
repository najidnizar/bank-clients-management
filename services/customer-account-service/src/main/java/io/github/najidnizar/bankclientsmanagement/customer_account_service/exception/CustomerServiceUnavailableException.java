package io.github.najidnizar.bankclientsmanagement.customer_account_service.exception;

public class CustomerServiceUnavailableException extends RuntimeException {

    public CustomerServiceUnavailableException(Throwable cause) {
        super("Customer service is currently unavailable", cause);
    }
}
