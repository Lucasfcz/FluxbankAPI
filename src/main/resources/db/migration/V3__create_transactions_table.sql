CREATE TABLE tb_transactions (
    id UUID PRIMARY KEY,
    from_account_id UUID NOT NULL,
    to_account_id UUID NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    type VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_transactions_from_account
        FOREIGN KEY (from_account_id) REFERENCES tb_accounts(id),
    CONSTRAINT fk_transactions_to_account
        FOREIGN KEY (to_account_id) REFERENCES tb_accounts(id)
);
