package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateCustomerResponse(

        UUID id,

        String customerNumber,

        String firstName,

        String lastName,

        String email

) {}
