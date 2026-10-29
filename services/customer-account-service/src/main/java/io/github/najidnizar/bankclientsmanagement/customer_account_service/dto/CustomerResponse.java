package io.github.najidnizar.bankclientsmanagement.customer_account_service.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CustomerResponse(

        Long version,

        UUID id,

        String customerNumber,

        String firstName,

        String lastName,

        String email,

        String phoneNumber,

        CustomerStatus status
) {}
