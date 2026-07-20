package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CustomerResponse(

        UUID id,

        String customerNumber,

        String firstName,

        String lastName,

        String email,

        String phoneNumber

) {}
