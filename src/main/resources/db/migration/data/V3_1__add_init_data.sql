-- ユーザー
INSERT INTO user_accounts (public_id, user_name, email, password, is_deleted)
VALUES
  ('a5d4e38d-dee5-4939-95e2-08b36400e3f3', 'Test User', 'demo@example.com', '$2a$10$moqqOz.7WsipJqzGsYXmR.hMr7oj6QLXFB3gnGlSRjh5kb0NqiwyC', false);

-- プロジェクト
INSERT INTO projects (user_account_id, public_id, project_caption, description, status, is_deleted)
VALUES
  (1, 'c640cace-b8f7-4ce4-8b11-3a47f6a2d62f', '新規開発プロジェクト', 'XXXシステム開発', 'ACTIVE', false);

-- タスク
INSERT INTO tasks (
  user_account_id, project_id, public_id, parent_task_id, task_caption, description,
  due_date, estimated_time, actual_time, progress, priority, is_deleted
)
VALUES
  (1, 1, 'afe04b78-8ba2-4425-ab90-48619209e55e', NULL,
   'タスクA', '', '2025-08-01', 200, 50, 25, 'HIGH', false),
  (1, 1, 'afe04b78-8ba2-4425-ab90-48619209e55f', 1,
    '設計', '', '2025-07-20', 100, 50, 50, 'HIGH', false),
  (1, 1, 'afe04b78-8ba2-4425-ab90-48619209e55g', 1,
    '実装', '', '2025-07-25', 100, 0, 0, 'HIGH', false);