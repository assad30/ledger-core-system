CREATE TABLE ledger_transactions (
                                     id BIGSERIAL PRIMARY KEY,
                                     transaction_reference UUID UNIQUE,
                                     transaction_type VARCHAR(30),
                                     parent_transaction_id BIGINT,
                                     idempotency_key VARCHAR(255) UNIQUE,
                                     created_at TIMESTAMP DEFAULT NOW()
);