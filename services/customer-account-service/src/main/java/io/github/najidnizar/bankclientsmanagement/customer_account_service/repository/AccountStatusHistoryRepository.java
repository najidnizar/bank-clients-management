package io.github.najidnizar.bankclientsmanagement.customer_account_service.repository;

import io.github.najidnizar.bankclientsmanagement.customer_account_service.domain.AccountStatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountStatusHistoryRepository extends JpaRepository<AccountStatusHistoryEntity, UUID> {

    List<AccountStatusHistoryEntity> findByAccountIdOrderByChangedAtAsc(UUID accountId);
}