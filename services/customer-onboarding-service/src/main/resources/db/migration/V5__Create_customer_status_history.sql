CREATE TABLE customer_status_history (
                                         id UUID PRIMARY KEY,
                                         customer_id UUID NOT NULL,
                                         old_status VARCHAR(20) NOT NULL,
                                         new_status VARCHAR(20) NOT NULL,
                                         changed_at TIMESTAMP NOT NULL,
                                         changed_by VARCHAR(100) NOT NULL,
                                         reason VARCHAR(500),

                                         CONSTRAINT fk_customer_status_history_customer
                                             FOREIGN KEY (customer_id)
                                                 REFERENCES customer(id)
);