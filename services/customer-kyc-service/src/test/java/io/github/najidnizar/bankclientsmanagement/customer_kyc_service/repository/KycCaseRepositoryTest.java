package io.github.najidnizar.bankclientsmanagement.customer_kyc_service.repository;

import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.config.AbstractPostgresIntegrationTest;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.entity.KycCaseEntity;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.entity.KycCaseStatus;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.entity.KycRiskLevel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class KycCaseRepositoryTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private KycCaseRepository kycCaseRepository;

    @Test
    void shouldPersistAndRetrieveKycCase() {

        UUID customerId = UUID.randomUUID();

        KycCaseEntity kycCase = KycCaseEntity.builder()
                .customerId(customerId)
                .status(KycCaseStatus.NOT_STARTED)
                .riskLevel(KycRiskLevel.LOW)
                .riskScore(20)
                .createdAt(LocalDateTime.now())
                .createdBy("SYSTEM")
                .updatedAt(LocalDateTime.now())
                .updatedBy("SYSTEM")
                .build();

        KycCaseEntity saved = kycCaseRepository.saveAndFlush(kycCase);

        Optional<KycCaseEntity> result = kycCaseRepository.findById(saved.getId());

        assertTrue(result.isPresent());

        KycCaseEntity found = result.get();

        assertEquals(customerId, found.getCustomerId());
        assertEquals(KycCaseStatus.NOT_STARTED, found.getStatus());
        assertEquals(KycRiskLevel.LOW, found.getRiskLevel());
        assertEquals(20, found.getRiskScore());
        assertEquals("SYSTEM", found.getCreatedBy());
        assertEquals("SYSTEM", found.getUpdatedBy());
    }
}