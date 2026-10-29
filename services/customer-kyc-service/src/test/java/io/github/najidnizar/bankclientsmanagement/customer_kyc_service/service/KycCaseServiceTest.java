package io.github.najidnizar.bankclientsmanagement.customer_kyc_service.service;

import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.dto.CreateKycCaseRequest;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.dto.KycCaseResponse;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.entity.KycCaseEntity;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.entity.KycCaseStatus;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.entity.KycRiskLevel;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.mapper.KycCaseMapper;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.repository.KycCaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class KycCaseServiceTest {

    @Mock
    private KycCaseRepository kycCaseRepository;

    @Mock
    private KycCaseMapper kycCaseMapper;

    @InjectMocks
    private KycCaseService kycCaseService;

    @Test
    void shouldCreateKycCase() {
        UUID customerId = UUID.randomUUID();

        CreateKycCaseRequest request = new CreateKycCaseRequest(customerId, KycRiskLevel.LOW, 20);
        KycCaseEntity entity = KycCaseEntity.builder()
                .customerId(customerId)
                .riskLevel(KycRiskLevel.LOW)
                .riskScore(20)
                .build();

        KycCaseEntity savedEntity = KycCaseEntity.builder()
                .id(UUID.randomUUID())
                .customerId(customerId)
                .status(KycCaseStatus.NOT_STARTED)
                .riskLevel(KycRiskLevel.LOW)
                .riskScore(20)
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .build();

        KycCaseResponse response = new KycCaseResponse(
                savedEntity.getId(),
                customerId,
                KycCaseStatus.NOT_STARTED,
                KycRiskLevel.LOW,
                20,
                savedEntity.getCreatedAt(),
                "SYSTEM",
                savedEntity.getUpdatedAt(),
                "SYSTEM"
        );

        when(kycCaseMapper.toEntity(request)).thenReturn(entity);
        when(kycCaseRepository.save(entity)).thenReturn(savedEntity);
        when(kycCaseMapper.toResponse(savedEntity)).thenReturn(response);

        KycCaseResponse result = kycCaseService.createKycCase(request);

        assertEquals(customerId, result.customerId());
        assertEquals(KycCaseStatus.NOT_STARTED, result.status());
        assertEquals(KycRiskLevel.LOW, result.riskLevel());

        verify(kycCaseRepository).save(entity);
        verify(kycCaseMapper).toResponse(savedEntity);
    }

    @Test
    void shouldMoveKycCaseFromNotStartedToInProgress() {

        UUID id = UUID.randomUUID();

        KycCaseEntity kycCase = KycCaseEntity.builder()
                .id(id)
                .customerId(UUID.randomUUID())
                .status(KycCaseStatus.NOT_STARTED)
                .riskLevel(KycRiskLevel.LOW)
                .riskScore(20)
                .build();

        KycCaseResponse response = new KycCaseResponse(
                id,
                kycCase.getCustomerId(),
                KycCaseStatus.IN_PROGRESS,
                KycRiskLevel.LOW,
                20,
                null,
                "SYSTEM",
                null,
                "SYSTEM"
        );

        when(kycCaseRepository.findById(id)).thenReturn(Optional.of(kycCase));
        when(kycCaseRepository.save(kycCase)).thenReturn(kycCase);
        when(kycCaseMapper.toResponse(kycCase)).thenReturn(response);

        KycCaseResponse result = kycCaseService.updateKycStatus(id, KycCaseStatus.IN_PROGRESS);

        assertEquals(KycCaseStatus.IN_PROGRESS, kycCase.getStatus());
        assertEquals(KycCaseStatus.IN_PROGRESS, result.status());

        verify(kycCaseRepository).save(kycCase);
    }

}