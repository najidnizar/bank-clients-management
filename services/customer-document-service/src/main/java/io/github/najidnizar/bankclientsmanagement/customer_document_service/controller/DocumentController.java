package io.github.najidnizar.bankclientsmanagement.customer_document_service.controller;

import io.github.najidnizar.bankclientsmanagement.customer_document_service.dto.DocumentResponse;
import io.github.najidnizar.bankclientsmanagement.customer_document_service.entity.DocumentType;
import io.github.najidnizar.bankclientsmanagement.customer_document_service.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam UUID customerId,
            @RequestParam DocumentType documentType,
            @RequestParam MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentService.uploadDocument(customerId, documentType, file));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<String> generateDownloadUrl(@PathVariable UUID id) {
        return ResponseEntity.ok(documentService.generateDownloadUrl(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable UUID id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }
}
