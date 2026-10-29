package io.github.najidnizar.bankclientsmanagement.customer_account_service.exception;

import io.github.najidnizar.bankclientsmanagement.customer_account_service.domain.AccountStatus;

public class InvalidAccountStatusTransitionException extends RuntimeException {

    public InvalidAccountStatusTransitionException(AccountStatus currentStatus, AccountStatus requestedStatus) {
        super("Invalid account status transition from " + currentStatus + " to " + requestedStatus);
    }
}
