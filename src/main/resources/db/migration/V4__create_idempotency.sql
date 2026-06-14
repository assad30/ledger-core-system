CREATE TABLE idempotency_keys (
                                  id BIGSERIAL PRIMARY KEY,
                                  key_value VARCHAR(255) UNIQUE,
                                  created_at TIMESTAMP DEFAULT NOW()
);