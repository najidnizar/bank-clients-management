package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.event;

import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerStatus;

import java.util.UUID;

public record CustomerEvent(
        CustomerEventType eventType,
        UUID customerId,
        String customerNumber,
        CustomerStatus status
) {
}
