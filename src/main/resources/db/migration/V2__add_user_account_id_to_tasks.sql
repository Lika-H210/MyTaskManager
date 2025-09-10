-- user_account_id カラム追加
ALTER TABLE tasks ADD COLUMN user_account_id INT DEFAULT NULL;

-- 既存データの更新（tasks.user_account_idにprojectsテーブルのuser_idと同値をセット）
UPDATE tasks t JOIN projects p ON t.project_id = p.id SET t.user_account_id = p.user_id;

-- NOT NULL 制約設定
ALTER TABLE tasks MODIFY COLUMN user_account_id INT NOT NULL;

-- 外部キー追加
ALTER TABLE tasks ADD CONSTRAINT fk_tasks_user FOREIGN KEY (user_account_id) REFERENCES user_accounts(id);

-- インデックス追加
ALTER TABLE tasks ADD KEY idx_user_account_id (user_account_id);