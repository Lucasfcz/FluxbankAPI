ALTER TABLE tb_accounts ADD COLUMN jwt_user_id BIGINT;

ALTER TABLE tb_accounts
ADD CONSTRAINT fk_account_jwt_user
FOREIGN KEY (jwt_user_id) REFERENCES jwt_user(id);

CREATE INDEX idx_account_jwt_user_id ON tb_accounts(jwt_user_id);
COMMENT ON COLUMN tb_accounts.jwt_user_id IS 'Foreign key to jwt_user. Every account must have authentication credentials.';

