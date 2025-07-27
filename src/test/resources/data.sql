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
