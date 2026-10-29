package io.github.najidnizar.bankclientsmanagement.customer_document_service.repository;

import io.github.najidnizar.bankclientsmanagement.customer_document_service.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DocumentRepository extends JpaRepository<DocumentEntity, UUID> {
}
