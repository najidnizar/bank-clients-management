package io.github.najidnizar.bankclientsmanagement.customer_document_service.dto;

import java.io.InputStream;

public record DocumentContent(String fileName, String contentType, long size, InputStream stream) {
}
