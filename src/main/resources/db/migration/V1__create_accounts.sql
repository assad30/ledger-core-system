CREATE TABLE accounts (
                          id BIGSERIAL PRIMARY KEY,
                          account_number VARCHAR(50) UNIQUE NOT NULL,
                          account_name VARCHAR(100),
                          account_type VARCHAR(30),
                          currency VARCHAR(10)
);