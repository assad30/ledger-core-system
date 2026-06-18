CREATE TABLE idempotency_keys (
      id BIGSERIAL PRIMARY KEY,
      key_value VARCHAR(255) NOT NULL UNIQUE,
      transaction_reference UUID,
      response JSONB,
      status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
      created_at TIMESTAMP DEFAULT NOW()
);