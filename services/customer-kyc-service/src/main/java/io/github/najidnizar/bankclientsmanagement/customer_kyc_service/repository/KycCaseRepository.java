package io.github.najidnizar.bankclientsmanagement.customer_kyc_service.repository;

import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.entity.KycCaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface KycCaseRepository extends JpaRepository<KycCaseEntity, UUID> {

}
