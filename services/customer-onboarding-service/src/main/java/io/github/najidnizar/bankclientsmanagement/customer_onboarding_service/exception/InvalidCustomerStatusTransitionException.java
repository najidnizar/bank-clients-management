package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.exception;

import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerStatus;

public class InvalidCustomerStatusTransitionException extends RuntimeException {

    public InvalidCustomerStatusTransitionException(CustomerStatus currentStatus, CustomerStatus targetStatus) {
        super("Cannot change customer status from " + currentStatus + " to " + targetStatus);
    }
}
