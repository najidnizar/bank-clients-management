package io.github.najidnizar.bankclientsmanagement.customer_kyc_service.exception;

import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.entity.KycCaseStatus;

import java.util.UUID;

public class KycCaseNotFoundException extends RuntimeException {

    public KycCaseNotFoundException(UUID customerId) {
        super("Kyc Case Not Found Exception for customer Id: " + customerId);
    }
}
