package io.github.najidnizar.bankclientsmanagement.customer_document_service.controller;

import io.github.najidnizar.bankclientsmanagement.customer_document_service.dto.DocumentContent;
import io.github.najidnizar.bankclientsmanagement.customer_document_service.dto.DocumentResponse;
import io.github.najidnizar.bankclientsmanagement.customer_document_service.entity.DocumentType;
import io.github.najidnizar.bankclientsmanagement.customer_document_service.service.DocumentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Tag(name = "Documents", description = "Customer document management APIs")
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADVISOR')")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam UUID customerId,
            @RequestParam DocumentType documentType,
            @RequestParam MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentService.uploadDocument(customerId, documentType, file));
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getDocumentsByCustomerId(@RequestParam UUID customerId) {
        return ResponseEntity.ok(documentService.getDocumentsByCustomerId(customerId));
    }

    @GetMapping(value = "/{id}/content", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadDocument(@PathVariable UUID id) {
        DocumentContent content = documentService.downloadDocument(id);
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(content.fileName(), StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(content.contentType()))
                .contentLength(content.size())
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(new InputStreamResource(content.stream()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable UUID id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }
}
