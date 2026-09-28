package io.github.najidnizar.bankclientsmanagement.customer_document_service.service;

import io.github.najidnizar.bankclientsmanagement.customer_document_service.dto.DocumentResponse;
import io.github.najidnizar.bankclientsmanagement.customer_document_service.entity.DocumentEntity;
import io.github.najidnizar.bankclientsmanagement.customer_document_service.entity.DocumentStatus;
import io.github.najidnizar.bankclientsmanagement.customer_document_service.entity.DocumentType;
import io.github.najidnizar.bankclientsmanagement.customer_document_service.exception.DocumentNotFoundException;
import io.github.najidnizar.bankclientsmanagement.customer_document_service.mapper.DocumentMapper;
import io.github.najidnizar.bankclientsmanagement.customer_document_service.repository.DocumentRepository;
import io.github.najidnizar.bankclientsmanagement.customer_document_service.storage.DocumentStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final DocumentStorageService documentStorageService;

    @Transactional
    public DocumentResponse uploadDocument(UUID customerId, DocumentType documentType, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Document file cannot be empty");
        }
        String storageKey = customerId + "/" + UUID.randomUUID();
        documentStorageService.upload(storageKey, file);

        DocumentEntity document = DocumentEntity.builder()
                .customerId(customerId)
                .documentType(documentType)
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .storageKey(storageKey)
                .status(DocumentStatus.UPLOADED)
                .createdAt(LocalDateTime.now())
                .createdBy("SYSTEM")
                .build();

        DocumentEntity saved = documentRepository.save(document);

        return documentMapper.toResponse(saved);
    }

    public String generateDownloadUrl(UUID documentId) {
        DocumentEntity document = documentRepository.findById(documentId).orElseThrow(() ->
                new DocumentNotFoundException(documentId));
        return documentStorageService.generateDownloadUrl(document.getStorageKey());
    }

    public DocumentResponse getDocumentById(UUID id) {
        DocumentEntity document = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id));
        return documentMapper.toResponse(document);
    }
}
