-- カラム名リネーム：user_id → user_account_id
ALTER TABLE projects RENAME COLUMN user_id TO user_account_id;

-- インデクス名の変更（インデックス削除 → 修正したインデックスを追加）
ALTER TABLE projects DROP INDEX idx_user_id, ADD INDEX idx_user_account_id (user_account_id);
