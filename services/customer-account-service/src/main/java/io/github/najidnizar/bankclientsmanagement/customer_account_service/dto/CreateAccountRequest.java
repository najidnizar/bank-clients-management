package io.github.najidnizar.bankclientsmanagement.customer_account_service.dto;

import io.github.najidnizar.bankclientsmanagement.customer_account_service.domain.AccountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record CreateAccountRequest(

        @NotNull
        UUID customerId,

        @NotNull
        AccountType accountType,

        @NotNull
        @Pattern(regexp = "^[A-Z]{3}$")
        String currencyCode
) {}
