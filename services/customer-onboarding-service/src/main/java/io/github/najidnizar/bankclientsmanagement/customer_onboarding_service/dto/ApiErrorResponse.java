package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ApiErrorResponse(

        Instant timestamp,

        int status,

        String error,

        String message,

        String path

) {
}
