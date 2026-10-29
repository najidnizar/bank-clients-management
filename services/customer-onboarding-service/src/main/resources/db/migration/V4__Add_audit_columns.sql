ALTER TABLE customer
    ADD COLUMN created_by VARCHAR(100);

ALTER TABLE customer
    ADD COLUMN updated_by VARCHAR(100);