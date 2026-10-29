package io.github.najidnizar.bankclientsmanagement.customer_account_service.repository;

import io.github.najidnizar.bankclientsmanagement.customer_account_service.config.AbstractPostgresIntegrationTest;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.domain.AccountStatus;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.domain.AccountType;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.domain.BankAccountEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BankAccountRepositoryIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Test
    void shouldSaveAndFindAccount() {

        UUID customerId = UUID.randomUUID();

        BankAccountEntity account = BankAccountEntity.builder()
                .customerId(customerId)
                .accountNumber("ACC-TEST-123456789")
                .accountType(AccountType.CURRENT)
                .status(AccountStatus.PENDING)
                .balance(BigDecimal.ZERO)
                .currencyCode("EUR")
                .createdAt(LocalDateTime.now())
                .createdBy("TEST")
                .updatedAt(LocalDateTime.now())
                .updatedBy("TEST")
                .build();

        BankAccountEntity saved = bankAccountRepository.save(account);

        BankAccountEntity found = bankAccountRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getCustomerId()).isEqualTo(customerId);
        assertThat(found.getAccountNumber()).isEqualTo("ACC-TEST-123456789");
        assertThat(found.getStatus()).isEqualTo(AccountStatus.PENDING);
        assertThat(found.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(found.getVersion()).isNotNull();
    }

    @Test
    void shouldInitializeVersionWhenAccountIsCreated() {

        BankAccountEntity account = BankAccountEntity.builder()
                .customerId(UUID.randomUUID())
                .accountNumber("ACC-VERSION-123")
                .accountType(AccountType.CURRENT)
                .status(AccountStatus.PENDING)
                .balance(BigDecimal.ZERO)
                .currencyCode("EUR")
                .createdAt(LocalDateTime.now())
                .createdBy("TEST")
                .updatedAt(LocalDateTime.now())
                .updatedBy("TEST")
                .build();

        BankAccountEntity saved = bankAccountRepository.saveAndFlush(account);

        assertThat(saved.getVersion()).isNotNull();
        assertThat(saved.getVersion()).isEqualTo(0L);
    }
}