package io.github.najidnizar.bankclientsmanagement.customer_account_service.repository;

import io.github.najidnizar.bankclientsmanagement.customer_account_service.domain.BankAccountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccountEntity, UUID> {

    boolean existsByAccountNumber(String accountNumber);
    Page<BankAccountEntity> findByCustomerId(UUID customerId, Pageable pageable);
}
