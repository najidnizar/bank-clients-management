package io.github.najidnizar.bankclientsmanagement.customer_kyc_service.service;

import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.dto.CreateKycCaseRequest;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.dto.KycCaseResponse;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.entity.KycCaseEntity;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.entity.KycCaseStatus;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.entity.KycRiskLevel;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.exception.InvalidKycStatusTransitionException;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.exception.KycCaseNotFoundException;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.mapper.KycCaseMapper;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.repository.KycCaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KycCaseService {

    private final KycCaseRepository kycCaseRepository;
    private final KycCaseMapper kycCaseMapper;

    @Transactional
    public KycCaseResponse createKycCase(CreateKycCaseRequest request) {

        KycCaseEntity kycCase = kycCaseMapper.toEntity(request);

        kycCase.setStatus(KycCaseStatus.NOT_STARTED);

        kycCase.setCreatedAt(LocalDateTime.now());
        kycCase.setCreatedBy("SYSTEM");
        kycCase.setUpdatedAt(LocalDateTime.now());
        kycCase.setUpdatedBy("SYSTEM");

        KycCaseEntity savedKycCase = kycCaseRepository.save(kycCase);

        return kycCaseMapper.toResponse(savedKycCase);
    }

    public KycCaseResponse getKycCaseById(UUID id) {

        KycCaseEntity kycCase = kycCaseRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("KYC case not found: " + id));

        return kycCaseMapper.toResponse(kycCase);
    }

    @Transactional
    public KycCaseResponse updateKycStatus(UUID id, KycCaseStatus requestedStatus) {
        KycCaseEntity kycCase = kycCaseRepository.findById(id).orElseThrow(() ->
                        new RuntimeException("KYC case not found: " + id));

        validateStatusTransition(kycCase.getStatus(), requestedStatus);

        kycCase.setStatus(requestedStatus);
        kycCase.setUpdatedAt(LocalDateTime.now());
        kycCase.setUpdatedBy("SYSTEM");

        KycCaseEntity updated = kycCaseRepository.save(kycCase);
        return kycCaseMapper.toResponse(updated);
    }

    private void validateStatusTransition(KycCaseStatus currentStatus, KycCaseStatus requestedStatus) {
        boolean valid = switch (currentStatus) {
            case NOT_STARTED -> requestedStatus == KycCaseStatus.IN_PROGRESS;
            case IN_PROGRESS -> requestedStatus == KycCaseStatus.PENDING_REVIEW;
            case PENDING_REVIEW -> requestedStatus == KycCaseStatus.APPROVED || requestedStatus == KycCaseStatus.REJECTED;
            case APPROVED, REJECTED, EXPIRED -> false;
        };
        if (!valid) {
            throw new InvalidKycStatusTransitionException(currentStatus, requestedStatus);
        }
    }

    @Transactional
    public KycCaseResponse createFromCustomer(UUID customerId) {

        if (kycCaseRepository.existsByCustomerId(customerId)) {
            KycCaseEntity caseEntity = kycCaseRepository.findByCustomerId(customerId).orElseThrow();
            return kycCaseMapper.toResponse(caseEntity);
        }

        KycCaseEntity kycCase = KycCaseEntity.builder()
                .customerId(customerId)
                .status(KycCaseStatus.NOT_STARTED)
                .riskLevel(KycRiskLevel.LOW)
                .riskScore(0)
                .createdAt(LocalDateTime.now())
                .createdBy("SYSTEM")
                .updatedAt(LocalDateTime.now())
                .updatedBy("SYSTEM")
                .build();

        KycCaseEntity savedCase = kycCaseRepository.save(kycCase);

        return kycCaseMapper.toResponse(savedCase);
    }

    @Transactional
    public void handleCustomerStatusChanged(UUID customerId, String customerStatus) {
        KycCaseEntity kycCase = kycCaseRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new KycCaseNotFoundException(customerId));

        if ("BLOCKED".equals(customerStatus)) {
            log.info("Customer {} is blocked, KYC case remains unchanged", customerId);
            return;
        }

        if ("CLOSED".equals(customerStatus)) {
            if (kycCase.getStatus() == KycCaseStatus.NOT_STARTED
                    || kycCase.getStatus() == KycCaseStatus.IN_PROGRESS) {
                kycCase.setStatus(KycCaseStatus.EXPIRED);
                kycCase.setUpdatedAt(LocalDateTime.now());
                kycCase.setUpdatedBy("SYSTEM");
                kycCaseRepository.save(kycCase);
            }
        }
    }
}
