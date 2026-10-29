package io.github.najidnizar.bankclientsmanagement.customer_kyc_service.exception;

import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.entity.KycCaseStatus;

public class InvalidKycStatusTransitionException extends RuntimeException {

    public InvalidKycStatusTransitionException(KycCaseStatus currentStatus, KycCaseStatus requestedStatus) {
        super("Invalid KYC status transition from " + currentStatus + " to " + requestedStatus);
    }
}
