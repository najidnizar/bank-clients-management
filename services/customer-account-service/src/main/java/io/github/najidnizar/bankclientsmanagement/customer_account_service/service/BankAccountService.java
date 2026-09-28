package io.github.najidnizar.bankclientsmanagement.customer_account_service.service;

import io.github.najidnizar.bankclientsmanagement.customer_account_service.client.CustomerServiceClient;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.domain.AccountStatus;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.domain.AccountStatusHistoryEntity;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.domain.BankAccountEntity;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.dto.AccountResponse;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.dto.CreateAccountRequest;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.exception.AccountNotFoundException;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.exception.CustomerNotFoundException;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.exception.InvalidAccountOperationException;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.exception.InvalidAccountStatusTransitionException;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.repository.AccountStatusHistoryRepository;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final AccountStatusHistoryRepository accountStatusHistoryRepository;
    private final CustomerServiceClient customerServiceClient;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {

        if (!customerServiceClient.customerExists(request.customerId())) {
            throw new CustomerNotFoundException(request.customerId());
        }

        BankAccountEntity account = BankAccountEntity.builder()
                .customerId(request.customerId())
                .accountNumber(generateAccountNumber())
                .accountType(request.accountType())
                .status(AccountStatus.PENDING)
                .balance(BigDecimal.ZERO)
                .currencyCode(request.currencyCode())
                .createdAt(LocalDateTime.now())
                .createdBy("SYSTEM")
                .updatedAt(LocalDateTime.now())
                .updatedBy("SYSTEM")
                .build();
        BankAccountEntity savedAccount = bankAccountRepository.save(account);

        saveStatusHistory(savedAccount.getId(), null, savedAccount.getStatus());

        return toResponse(savedAccount);
    }

    private void saveStatusHistory(UUID accountId, AccountStatus previousStatus, AccountStatus newStatus) {
        AccountStatusHistoryEntity history =
                AccountStatusHistoryEntity.builder()
                        .accountId(accountId)
                        .previousStatus(previousStatus)
                        .newStatus(newStatus)
                        .changedAt(LocalDateTime.now())
                        .changedBy("SYSTEM")
                        .build();
        accountStatusHistoryRepository.save(history);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountById(UUID id) {
        BankAccountEntity account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        return toResponse(account);
    }

    @Transactional(readOnly = true)
    public Page<AccountResponse> getAccountsByCustomerId(UUID customerId, Pageable pageable) {
        return bankAccountRepository.findByCustomerId(customerId, pageable).map(this::toResponse);
    }

    private String generateAccountNumber() {
        return "ACC-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 20)
                .toUpperCase();
    }

    private AccountResponse toResponse(BankAccountEntity account) {
        return new AccountResponse(
                account.getId(),
                account.getCustomerId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getStatus(),
                account.getBalance(),
                account.getCurrencyCode(),
                account.getCreatedAt(),
                account.getCreatedBy(),
                account.getUpdatedAt(),
                account.getUpdatedBy()
        );
    }

    @Transactional
    public AccountResponse updateAccountStatus(UUID id, AccountStatus requestedStatus) {

        BankAccountEntity account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        validateStatusTransition(account.getStatus(), requestedStatus);

        final AccountStatus previousAccountStatus = account.getStatus();

        if (requestedStatus == AccountStatus.CLOSED && account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new InvalidAccountOperationException("Account cannot be closed while balance is not zero");
        }

        account.setStatus(requestedStatus);
        account.setUpdatedAt(LocalDateTime.now());
        account.setUpdatedBy("SYSTEM");

        BankAccountEntity savedAccount = bankAccountRepository.save(account);
        saveStatusHistory(savedAccount.getId(), previousAccountStatus, requestedStatus);

        return toResponse(savedAccount);
    }

    private void validateStatusTransition(AccountStatus currentStatus, AccountStatus requestedStatus) {

        boolean valid = switch (currentStatus) {
            case PENDING -> requestedStatus == AccountStatus.ACTIVE;
            case ACTIVE -> requestedStatus == AccountStatus.BLOCKED || requestedStatus == AccountStatus.CLOSED;
            case BLOCKED -> requestedStatus == AccountStatus.ACTIVE || requestedStatus == AccountStatus.CLOSED;
            case CLOSED -> false;
        };

        if (!valid) {
            throw new InvalidAccountStatusTransitionException(currentStatus, requestedStatus);
        }
    }
}
