CREATE TABLE ledger_entries (
                                id BIGSERIAL PRIMARY KEY,
                                transaction_id BIGINT,
                                account_id BIGINT,
                                entry_type VARCHAR(10),
                                amount NUMERIC(18,2),
                                created_at TIMESTAMP DEFAULT NOW()
);