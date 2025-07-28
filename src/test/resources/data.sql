-- ユーザー
INSERT INTO user_accounts (public_id, user_name, email, password_hash)
VALUES
  ('5e8c0d2a-1234-4f99-a111-abcdef111111', '田中太郎', 'tanaka@example.com', 'hashed_pw_tanaka'),
  ('9f6b0f3b-5678-4a8a-b222-abcdef222222', '佐藤花子', 'sato@example.com', 'hashed_pw_sato');

-- プロジェクト
INSERT INTO projects (user_id, public_id, project_caption, description, status)
VALUES
  (1, 'a1111111-bbbb-cccc-dddd-eeeeeeeeeeee', '勤怠管理システム', '社内用の勤怠管理ツール', 'ACTIVE'),
  (1, 'a2222222-bbbb-cccc-dddd-eeeeeeeeeeee', '顧客管理アプリ', '顧客情報を一元管理するCRMシステム', 'ACTIVE'),
  (2, 'b1111111-cccc-dddd-eeee-ffffffffffff', 'ECサイト構築', 'ネットショップの構築プロジェクト', 'ARCHIVED');

-- タスク
INSERT INTO tasks (
  project_id, public_id, parent_task_id, task_caption, description,
  due_date, estimated_time, actual_time_minutes, progress, priority
)
VALUES
  (1, '11111111-aaaa-bbbb-cccc-1234567890ab', NULL,
   'データベース設計', 'テーブルとリレーションの設計を行う', '2025-08-01', 200, 250, 100, 'HIGH'),

  (1, '22222222-bbbb-cccc-dddd-1234567890ab', 1,
   'ER図レビュー', 'レビューをチームで実施', '2025-08-02', 120, 120, 80, 'MEDIUM'),

  (2, '33333333-cccc-dddd-eeee-1234567890ab', NULL,
   '画面モック作成', 'FigmaでUIの初期デザインを作成', '2025-08-05', 60, 25, 80, 'LOW'),

  (3, '44444444-dddd-eeee-ffff-1234567890ab', NULL,
   '商品登録機能の実装', '商品のCRUD機能を実装', '2025-08-10', 200, 180, 100, 'HIGH');
