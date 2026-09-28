CREATE TABLE bank_account (
                              id UUID PRIMARY KEY,
                              customer_id UUID NOT NULL,
                              account_number VARCHAR(34) NOT NULL UNIQUE,
                              account_type VARCHAR(20) NOT NULL,
                              status VARCHAR(20) NOT NULL,
                              balance NUMERIC(19, 4) NOT NULL,
                              currency_code VARCHAR(3) NOT NULL,
                              created_at TIMESTAMP NOT NULL,
                              created_by VARCHAR(100) NOT NULL,
                              updated_at TIMESTAMP NOT NULL,
                              updated_by VARCHAR(100) NOT NULL,
                              version BIGINT NOT NULL
);