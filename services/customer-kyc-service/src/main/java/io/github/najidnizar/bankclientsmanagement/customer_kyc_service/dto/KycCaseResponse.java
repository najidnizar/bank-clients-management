package io.github.najidnizar.bankclientsmanagement.customer_kyc_service.dto;

import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.entity.KycCaseStatus;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.entity.KycRiskLevel;

import java.time.LocalDateTime;
import java.util.UUID;

public record KycCaseResponse(

        UUID id,
        UUID customerId,
        KycCaseStatus status,
        KycRiskLevel riskLevel,
        Integer riskScore,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime updatedAt,
        String updatedBy

) {
}
