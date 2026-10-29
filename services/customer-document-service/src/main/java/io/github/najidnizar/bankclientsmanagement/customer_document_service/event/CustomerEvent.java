package io.github.najidnizar.bankclientsmanagement.customer_document_service.event;

import java.util.UUID;

public record CustomerEvent(
        CustomerEventType eventType,
        UUID customerId,
        String customerNumber,
        String status
) {
}
