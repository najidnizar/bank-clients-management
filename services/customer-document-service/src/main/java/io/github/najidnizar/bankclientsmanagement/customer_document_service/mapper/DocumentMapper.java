package io.github.najidnizar.bankclientsmanagement.customer_document_service.mapper;

import io.github.najidnizar.bankclientsmanagement.customer_document_service.dto.DocumentResponse;
import io.github.najidnizar.bankclientsmanagement.customer_document_service.entity.DocumentEntity;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {

    public DocumentResponse toResponse(DocumentEntity entity) {
        return new DocumentResponse(
                entity.getId(),
                entity.getCustomerId(),
                entity.getDocumentType(),
                entity.getFileName(),
                entity.getContentType(),
                entity.getFileSize(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getCreatedBy()
        );
    }
}
