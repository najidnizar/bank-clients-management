package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.repository;

import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerStatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerStatusHistoryRepository extends JpaRepository<CustomerStatusHistoryEntity, UUID> {
}
