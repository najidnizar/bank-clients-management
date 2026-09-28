package io.github.najidnizar.bankclientsmanagement.customer_document_service.dto;

import io.github.najidnizar.bankclientsmanagement.customer_document_service.entity.DocumentStatus;
import io.github.najidnizar.bankclientsmanagement.customer_document_service.entity.DocumentType;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentResponse(
        UUID id,
        UUID customerId,
        DocumentType documentType,
        String fileName,
        String contentType,
        Long fileSize,
        DocumentStatus status,
        LocalDateTime createdAt,
        String createdBy
) {
}
