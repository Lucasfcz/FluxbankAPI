ALTER TABLE tb_transactions ALTER COLUMN from_account_id DROP NOT NULL;
ALTER TABLE tb_transactions ALTER COLUMN to_account_id DROP NOT NULL;