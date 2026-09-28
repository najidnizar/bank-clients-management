package io.github.najidnizar.bankclientsmanagement.customer_document_service.storage;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class DocumentStorageService {

    private static final String BUCKET_NAME = "customer-documents";

    private final MinioClient minioClient;

    public void upload(String storageKey, MultipartFile file) {
        try {
            minioClient.putObject(PutObjectArgs.builder().bucket(BUCKET_NAME).object(storageKey).stream(
                            file.getInputStream(),
                            file.getSize(),
                            -1
                            )
                    .contentType(file.getContentType())
                    .build()
            );
        } catch (Exception exception) {
            throw new RuntimeException("Failed to upload document to storage", exception);
        }
    }

    public String generateDownloadUrl(String storageKey) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(BUCKET_NAME)
                    .object(storageKey)
                    .expiry(15, TimeUnit.MINUTES)
                    .build()
            );
        } catch (Exception exception) {
            throw new RuntimeException("Failed to generate document download URL", exception);
        }
    }
}