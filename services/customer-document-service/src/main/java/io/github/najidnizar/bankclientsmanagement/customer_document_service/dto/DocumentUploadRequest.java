package io.github.najidnizar.bankclientsmanagement.customer_document_service.dto;

import io.github.najidnizar.bankclientsmanagement.customer_document_service.entity.DocumentType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DocumentUploadRequest(

        @NotNull
        UUID customerId,

        @NotNull
        DocumentType documentType

) {
}
