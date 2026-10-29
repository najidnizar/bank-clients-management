CREATE TABLE account_status_history (
                                        id UUID PRIMARY KEY,
                                        account_id UUID NOT NULL,
                                        previous_status VARCHAR(20),
                                        new_status VARCHAR(20) NOT NULL,
                                        changed_at TIMESTAMP NOT NULL,
                                        changed_by VARCHAR(100) NOT NULL
);

CREATE INDEX idx_account_status_history_account_id
    ON account_status_history(account_id);