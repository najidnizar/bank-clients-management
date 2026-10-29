CREATE TABLE customer (
                          id UUID PRIMARY KEY,
                          customer_number VARCHAR(10) NOT NULL,
                          first_name VARCHAR(100) NOT NULL,
                          last_name VARCHAR(100) NOT NULL,
                          date_of_birth DATE NOT NULL,
                          email VARCHAR(255) NOT NULL,
                          phone_number VARCHAR(30) NOT NULL,
                          created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                          updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

                          CONSTRAINT uk_customer_number UNIQUE (customer_number),
                          CONSTRAINT uk_customer_email UNIQUE (email)
);