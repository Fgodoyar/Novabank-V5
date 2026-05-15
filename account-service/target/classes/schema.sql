CREATE SEQUENCE IF NOT EXISTS accounts_account_id_seq;

CREATE TABLE IF NOT EXISTS accounts
(
    account_id     BIGINT         NOT NULL DEFAULT nextval('accounts_account_id_seq'),
    account_holder VARCHAR(255),
    account_number VARCHAR(255)   UNIQUE,
    balance        NUMERIC(15, 2) DEFAULT 0,
    creation_date  TIMESTAMP,
    customer_id    BIGINT         NOT NULL,
    CONSTRAINT accounts_pkey PRIMARY KEY (account_id)
    );

CREATE SEQUENCE IF NOT EXISTS transactions_transaction_id_seq;

CREATE TABLE IF NOT EXISTS transactions
(
    transaction_id   BIGINT         NOT NULL DEFAULT nextval('transactions_transaction_id_seq'),
    amount           NUMERIC(15, 2),
    creation_date    TIMESTAMP,
    description      VARCHAR(255),
    transaction_type VARCHAR(255),
    account_id       BIGINT,
    CONSTRAINT transactions_pkey PRIMARY KEY (transaction_id),
    CONSTRAINT fk_transactions_account FOREIGN KEY (account_id)
    REFERENCES accounts (account_id)
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    );