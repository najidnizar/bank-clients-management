CREATE TABLE document (
                          id UUID PRIMARY KEY,
                          customer_id UUID NOT NULL,

                          document_type VARCHAR(30) NOT NULL,
                          file_name VARCHAR(255) NOT NULL,
                          content_type VARCHAR(100) NOT NULL,
                          file_size BIGINT NOT NULL,
                          storage_key VARCHAR(500) NOT NULL UNIQUE,

                          status VARCHAR(20) NOT NULL,

                          created_at TIMESTAMP NOT NULL,
                          created_by VARCHAR(100) NOT NULL
);