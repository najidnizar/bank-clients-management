package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UpdateCustomerRequest(
        @NotBlank
        @Size(max = 100)
        String firstName,

        @NotBlank
        @Size(max = 100)
        String lastName,

        @NotNull
        LocalDate dateOfBirth,

        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @NotBlank
        String phoneNumber
) {}
