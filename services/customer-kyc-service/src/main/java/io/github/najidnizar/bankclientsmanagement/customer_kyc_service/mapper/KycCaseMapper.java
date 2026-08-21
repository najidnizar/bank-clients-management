package io.github.najidnizar.bankclientsmanagement.customer_kyc_service.mapper;


import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.dto.CreateKycCaseRequest;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.dto.KycCaseResponse;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.entity.KycCaseEntity;
import org.springframework.stereotype.Component;

@Component
public class KycCaseMapper {

    public KycCaseEntity toEntity(CreateKycCaseRequest request) {
        return KycCaseEntity.builder()
                .customerId(request.customerId())
                .riskLevel(request.riskLevel())
                .riskScore(request.riskScore())
                .build();
    }

    public KycCaseResponse toResponse(KycCaseEntity entity) {
        return new KycCaseResponse(
                entity.getId(),
                entity.getCustomerId(),
                entity.getStatus(),
                entity.getRiskLevel(),
                entity.getRiskScore(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedAt(),
                entity.getUpdatedBy()
        );
    }
}
