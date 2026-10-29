CREATE TABLE kyc_case (
                          id UUID PRIMARY KEY,
                          customer_id UUID NOT NULL,

                          status VARCHAR(30) NOT NULL,
                          risk_level VARCHAR(20) NOT NULL,
                          risk_score INTEGER,

                          created_at TIMESTAMP NOT NULL,
                          created_by VARCHAR(100) NOT NULL,
                          updated_at TIMESTAMP NOT NULL,
                          updated_by VARCHAR(100) NOT NULL
);