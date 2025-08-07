-- ユーザー
INSERT INTO user_accounts (public_id, user_name, email, password, is_deleted)
VALUES
  ('5e8c0d2a-1234-4f99-a111-abcdef111111', '田中太郎', 'tanaka@example.com', 'hashed_pw_tanaka', false),
  ('9f6b0f3b-5678-4a8a-b222-abcdef222222', '佐藤花子', 'sato@example.com', 'hashed_pw_sato', false),
  ('12345678-90ab-cdef-1234-abcdef123456', '削除太郎', 'sakujo@example.com', 'hashed_pw_deleted', true);

-- プロジェクト
INSERT INTO projects (user_id, public_id, project_caption, description, status, is_deleted)
VALUES
  (1, 'a1111111-bbbb-cccc-dddd-eeeeeeeeeeee', '勤怠管理システム', '社内用の勤怠管理ツール', 'ACTIVE', false),
  (1, 'a2222222-bbbb-cccc-dddd-eeeeeeeeeeee', '顧客管理アプリ', '顧客情報を一元管理するCRMシステム', 'ACTIVE', false),
  (2, 'b1111111-cccc-dddd-eeee-ffffffffffff', 'ECサイト構築', 'ネットショップの構築プロジェクト', 'ARCHIVED', false),
  (2, 'b2222222-cccc-dddd-eeee-ffffffffffff', 'WEBサイト更新', 'WEBサイトの更新プロジェクト', 'ACTIVE', false),
  (2, 'b3333333-cccc-dddd-eeee-ffffffffffff', 'イベント企画', '社内イベントの準備', 'ACTIVE', true);

-- タスク
INSERT INTO tasks (
  project_id, public_id, parent_task_id, task_caption, description,
  due_date, estimated_time, actual_time, progress, priority, is_deleted
)
VALUES
  (1, '11111111-aaaa-bbbb-cccc-1234567890ab', NULL,
   'データベース設計', 'テーブルとリレーションの設計を行う', '2025-08-01', 200, 250, 100, 'HIGH', false),

  (1, '22222222-bbbb-cccc-dddd-1234567890ab', 1,
   'ER図レビュー', 'レビューをチームで実施', '2025-08-02', 120, 120, 80, 'MEDIUM', false),

  (2, '33333333-cccc-dddd-eeee-1234567890ab', NULL,
   '画面モック作成', 'FigmaでUIの初期デザインを作成', '2025-08-05', 120, 60, 50, 'MEDIUM', false),

  (2, '55555555-eeee-ffff-0000-1234567890ab', 3,
   'UIコンポーネントの設計', '共通UIコンポーネントのレイアウト設計を行う', '2025-08-06', 60, 60, 100, 'MEDIUM', true),

  (2, '66666666-ffff-0000-1111-1234567890ab', 3,
   'フィードバック対応', 'レビュー結果を反映してUIを調整する', '2025-08-07', 60, 0, 0, 'LOW', false),

  (3, '44444444-dddd-eeee-ffff-1234567890ab', NULL,
   '商品登録機能の実装', '商品のCRUD機能を実装', '2025-08-10', 200, 180, 100, 'HIGH', false);
