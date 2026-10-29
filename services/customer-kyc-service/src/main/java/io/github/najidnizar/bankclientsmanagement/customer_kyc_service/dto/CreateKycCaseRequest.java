package io.github.najidnizar.bankclientsmanagement.customer_kyc_service.dto;

import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.entity.KycRiskLevel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateKycCaseRequest(

        @NotNull
        UUID customerId,

        @NotNull
        KycRiskLevel riskLevel,

        @Min(0)
        @Max(100)
        Integer riskScore

) {
}
