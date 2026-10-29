package io.github.najidnizar.bankclientsmanagement.customer_account_service.dto;

import io.github.najidnizar.bankclientsmanagement.customer_account_service.domain.AccountStatus;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.domain.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        UUID customerId,
        String accountNumber,
        AccountType accountType,
        AccountStatus status,
        BigDecimal balance,
        String currencyCode,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime updatedAt,
        String updatedBy
) {}
