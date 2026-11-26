package io.github.najidnizar.bankclientsmanagement.customer_kyc_service.exception;

import java.util.UUID;

public class KycCaseNotFoundException extends RuntimeException {

    private KycCaseNotFoundException(String message) {
        super(message);
    }

    public static KycCaseNotFoundException forId(UUID id) {
        return new KycCaseNotFoundException("KYC case not found: " + id);
    }

    public static KycCaseNotFoundException forCustomer(UUID customerId) {
        return new KycCaseNotFoundException("No KYC case for customer: " + customerId);
    }
}
