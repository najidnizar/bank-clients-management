package io.github.najidnizar.bankclientsmanagement.customer_document_service.controller;

import io.github.najidnizar.bankclientsmanagement.customer_document_service.config.SecurityConfig;
import io.github.najidnizar.bankclientsmanagement.customer_document_service.dto.DocumentContent;
import io.github.najidnizar.bankclientsmanagement.customer_document_service.dto.DocumentResponse;
import io.github.najidnizar.bankclientsmanagement.customer_document_service.exception.DocumentNotFoundException;
import io.github.najidnizar.bankclientsmanagement.customer_document_service.entity.DocumentStatus;
import io.github.najidnizar.bankclientsmanagement.customer_document_service.entity.DocumentType;
import io.github.najidnizar.bankclientsmanagement.customer_document_service.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
@Import(SecurityConfig.class)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentService documentService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void shouldListCustomerDocuments() throws Exception {
        UUID customerId = UUID.randomUUID();
        DocumentResponse document = new DocumentResponse(UUID.randomUUID(), customerId, DocumentType.PASSPORT,
                "passport.pdf", "application/pdf", 1024L, DocumentStatus.UPLOADED, LocalDateTime.now(), "advisor");
        when(documentService.getDocumentsByCustomerId(customerId)).thenReturn(List.of(document));

        mockMvc.perform(get("/api/v1/documents").param("customerId", customerId.toString()).with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fileName").value("passport.pdf"));
    }

    @Test
    void shouldStreamDocumentAsAttachment() throws Exception {
        UUID documentId = UUID.randomUUID();
        byte[] bytes = "pdf-content".getBytes();
        when(documentService.downloadDocument(documentId)).thenReturn(
                new DocumentContent("passport.pdf", "application/pdf", bytes.length, new ByteArrayInputStream(bytes)));

        mockMvc.perform(get("/api/v1/documents/{id}/content", documentId).with(jwt()))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("passport.pdf")))
                .andExpect(content().contentType("application/pdf"))
                .andExpect(content().bytes(bytes));
    }

    @Test
    void shouldReturnNotFoundAsJsonWhenDownloadingUnknownDocument() throws Exception {
        UUID documentId = UUID.randomUUID();
        when(documentService.downloadDocument(documentId)).thenThrow(new DocumentNotFoundException(documentId));

        mockMvc.perform(get("/api/v1/documents/{id}/content", documentId)
                        .accept(MediaType.APPLICATION_OCTET_STREAM)
                        .with(jwt()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldRejectUnauthenticatedRequests() throws Exception {
        mockMvc.perform(get("/api/v1/documents").param("customerId", UUID.randomUUID().toString()))
                .andExpect(status().isUnauthorized());
    }
}
