package io.github.najidnizar.bankclientsmanagement.customer_account_service.dto;

import io.github.najidnizar.bankclientsmanagement.customer_account_service.domain.AccountStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record AccountStatusHistoryResponse(
        UUID id,
        UUID accountId,
        AccountStatus previousStatus,
        AccountStatus newStatus,
        LocalDateTime changedAt,
        String changedBy
) {}
