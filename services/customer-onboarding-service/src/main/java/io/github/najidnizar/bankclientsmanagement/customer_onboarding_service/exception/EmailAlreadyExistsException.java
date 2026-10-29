package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("Email '" + email + "' already exists.");
    }

}
