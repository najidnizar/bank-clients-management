package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CreateCustomerRequest(

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotNull
        LocalDate dateOfBirth,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String phoneNumber
) {}
