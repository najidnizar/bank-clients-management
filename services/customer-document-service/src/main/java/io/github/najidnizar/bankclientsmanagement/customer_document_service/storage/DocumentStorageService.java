package io.github.najidnizar.bankclientsmanagement.customer_document_service.storage;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class DocumentStorageService {

    private static final String BUCKET_NAME = "customer-documents";

    private final MinioClient minioClient;

    public void upload(String storageKey, MultipartFile file) {
        try {
            createBucketIfMissing();
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

    public InputStream download(String storageKey) {
        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket(BUCKET_NAME).object(storageKey).build());
        } catch (Exception exception) {
            throw new RuntimeException("Failed to download document from storage", exception);
        }
    }

    private void createBucketIfMissing() throws Exception {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
        }
    }
}
