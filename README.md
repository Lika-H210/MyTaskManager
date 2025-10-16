# MyTaskManager

## はじめに

本リポジトリは、個人ユーザー向けタスク管理アプリのバックエンド実装です。

本リポジトリのコードは学習・ポートフォリオ目的で提供しており、  
利用に関するトラブル等については責任を負いかねますのでご了承ください。

## 背景

個人のタスク管理は業務を的確に遂行するうえで重要です。
しかし、企業で導入されている業務管理システムは、プロジェクト管理や進捗報告を目的としており、「タスクを実行」という本来の目的以外の情報入力に時間を取られていると感じていました。
<br>そこで本アプリでは、業務タスクの実行に注力し、実務時間の確保と継続的なタスク管理を両立できるアプリを目指して開発に取り組みました。

## 概要

本リポジトリは、個人ユーザー向けタスク管理アプリのバックエンドで、タスク・プロジェクト管理、ユーザー認証、DB連携などを提供します。
<br>
設計の重点は以下の通りです：

- プロジェクト単位でのタスク整理
- 親子タスクによる階層的な管理
- タスク実行に必要な情報（期限、進捗、見積時間）のみに絞った入力項目

※補足：デモ用の簡易フロントも同梱していますが、フロントは主に生成AIを利用して作成しています。
<br>
<br>

## 技術スタック

- **バックエンド**: Java 21, Spring Boot 3.5.3
- **DB**: MySQL 8.0（テストのみ H2 DB）
- **ORM / SQL Mapper**: MyBatis 3.0.5
- **セキュリティ**: Spring Security
- **DB マイグレーション**: Flyway 11.12.0
- **ドキュメント**: OpenAPI 3.1 / Springdoc 2.8.9
- **テスト**: JUnit 5, Spring Boot Test
  <br>
  <br>

## インストール・起動手順

<details>
<summary> Dockerを利用する場合 </summary>

以下は、Dockerがインストール済みであることを前提にした起動方法です。

### 1. リポジトリをクローン

任意のフォルダにリポジトリをクローンします。

```bash
git clone https://github.com/Lika-H210/MyTaskManager.git
cd MyTaskManager
```

### 2. Docker Composeでアプリを起動

```bash
docker compose up -d
```

- 初回起動時はコンテナが作成され、DBも構築されます。
- 既にコンテナが存在する場合は再利用して起動します。

**補足:**

- Spring Boot はポート 8080、MySQL はホスト側 3307 を使用します。
- 既に使用中の場合は `docker-compose.yml` 内のホスト側ポートを変更してください。

### 3. 動作確認

- ブラウザからの動作確認：http://localhost:8080/login.html

    - 下記デモユーザー情報でのログインも可能です。
        - Email: demo@example.com
        - パスワード: demo_password  
          <br>
- API仕様書の確認：http://localhost:8080/swagger-ui/index.html

### Docker停止・終了

- 一時的に停止させる場合（再開可能）

```bash
docker compose stop
```

- 完全に終了し、コンテナを削除

```bash
docker compose down
```

</details> 

<details>
<summary> Dockerを利用しない場合 </summary>

### 1. リポジトリをクローン

任意のフォルダにリポジトリをクローンします。

```bash
git clone https://github.com/Lika-H210/MyTaskManager.git
cd MyTaskManager
```

### 2. データベースの準備

- MySQLをインストールします。（バージョン 8.0）  
  https://www.mysql.com/jp/downloads/
- 下記のDBを作成します。<br>
  データベース名：task_management

### 3. application.properties の変更

- クローンフォルダ内の`src/main/resources/application.properties` の MySQL と Flyway
  設定箇所を、下記の内容に置き換えます。  
  この際、下記の "登録したパスワード" は手順2のMySQLインストール時に設定したパスワードに変更してください。

```properties
# MySQL
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/task_management
spring.datasource.username=root
spring.datasource.password=登録したパスワード
# Flyway
spring.flyway.locations=classpath:db/migration/schema,classpath:db/migration/data
spring.flyway.enabled=true
```

### 4. SpringBootを起動

SpringBootを起動します。

```bash
./gradlew bootRun
```

**補足:**

- Spring Boot はポート 8080、MySQL はホスト側 3306 を使用します。
- 既に使用中の場合は 、application.properties 内でポート番号を変更して起動してください。

### 5. 動作確認

- ブラウザからの動作確認：http://localhost:8080/login.html

    - 下記デモユーザー情報でのログインも可能です。
        - Email: demo@example.com
        - パスワード: demo_password  
          <br>
- API仕様書の確認：http://localhost:8080/swagger-ui/index.html

</details> 
<br>

## 動作イメージ

### タスク管理

#### ログイン → 各種一覧取得

https://github.com/user-attachments/assets/d42a9913-9417-4fdf-89a5-f1ec745a0c25

<br>

#### プロジェクトの登録・更新・削除

https://github.com/user-attachments/assets/6d23ceee-7fcf-44c5-aa20-aadaa3534ffc

<br>

#### 親タスクの登録・更新・削除

https://github.com/user-attachments/assets/a7782682-5620-429e-8c1d-b882019a76f4

<br>

### アカウント情報

#### アカウント登録

https://github.com/user-attachments/assets/dc03b39c-7189-4070-818d-20423eb3bde3

<br>

#### メールアドレス更新
　プロジェクト一覧　→　アカウント一覧 → メールアドレス更新

https://github.com/user-attachments/assets/ca24a4c9-864c-4919-b1cc-bb41c9e1b75e

<br>

### その他

#### バリデーション

<img width="686" height="231" alt="image" src="https://github.com/user-attachments/assets/12367e19-8bda-4e5b-b36c-2e92cc6c2d91" />
<br>
400エラーのレスポンス情報を基にエラーメッセージを表示します。（※例示はアドレスの重複登録による400エラー）
<br>
<br>

## 実装機能の概要

- ユーザー認証　　　（ログイン/ログアウト）
- アカウント情報管理（取得 / 登録 / 更新 / 削除）
- プロジェクト管理　（取得 / 登録 / 更新 / 削除）
- タスク管理　　　　（取得 / 登録 / 更新 / 削除）
  <br>

### API 概要

### User

| メソッド   | パス              | 説明                |
|--------|-----------------|-------------------|
| GET    | /users/me       | ログインユーザー情報取得      |
| POST   | /users/register | 新規ユーザー登録          |
| PUT    | /users/me/info  | ユーザー情報更新          |
| DELETE | /users/me       | ユーザーアカウント削除（論理削除） |

### Project

| メソッド   | パス                    | 説明             |
|--------|-----------------------|----------------|
| GET    | /projects             | プロジェクト一覧取得     |
| GET    | /projects/{projectId} | 単独プロジェクト取得     |
| POST   | /projects             | 新規プロジェクト登録     |
| PUT    | /projects/{projectId} | プロジェクト更新       |
| DELETE | /projects/{projectId} | プロジェクト削除（論理削除） |

### Task

| メソッド   | パス                               | 説明          |
|--------|----------------------------------|-------------|
| GET    | /projects/{projectId}/task-trees | 親子タスク一覧取得   |
| GET    | /tasks/{taskId}                  | 単体タスク取得     |
| POST   | /projects/{projectId}/tasks      | 親タスク登録      |
| PUT    | /tasks/{taskId}                  | タスク更新       |
| DELETE | /tasks/{taskId}                  | タスク削除（論理削除） |

## ER図

<img width="351" height="497" alt="mKHFjqrNbX" src="https://github.com/user-attachments/assets/9b14d7c5-0d56-4aee-abdd-f658e7dc06d5" />
<br>

## セキュリティ対策

- 認証・認可: セッションベース認証 + 所有権チェック（403エラーで不正アクセス検知）
- CSRF:CSRF保護を有効化した上で、トークン取得APIを通じてフロント側から正常に操作できるようにした実装
- SQLインジェクション: MyBatisのプリペアドステートメント（`#{}`）を使用
- パスワードセキュリティ: BCryptPasswordEncoderによるハッシュ化
- セッションタイムアウト: 非アクティブ時のセッション無効化設定（30分）
  <br>

## 自動テスト

- 単体テスト：
    - Controller（認証不要APIのみ）、Service、Repository、Converter のロジック検証
    - 各リクエストDTOのバリデーションテスト
      <br>
      <br>
- 結合テスト：
    - Controller・SecurityConfig・例外ハンドリングを対象
        - MockMvc により、認証情報・CSRF・例外応答の動作確認
        - Service はモック化し、DBアクセスはテスト対象外
          <br>

## 動作確認環境

- **OS**: Windows 11
- **JDK**: 21
- **ビルドツール**: Gradle 8.14.3
- **ブラウザ**: Chrome 140.x
  <br>

## 実装のポイント

<details>
<summary> 実装のポイントを確認する（クリックで表示） </summary>

### 1. ログイン機能の実装

- ログイン情報に基づきデータの所有権を確認することで、他ユーザーのデータへのアクセスを防いでいます。
- ログインの実装は容易さを重視し **セッションベースの認証方式** を採用しています。<br>
- Spring Securityの標準機能を活用することで、実装の信頼性を確保しています。
  <br>

### 2. 親子関係をもつ階層構造テーブル設計

- タスクテーブルは階層構造を持つ設計になっており、一つのテーブルでタスク管理をしています。  
  これにより、DB変更時の影響範囲を限定できます。
- 階層構造を意識した実装することで、将来的に階層を拡張できる設計になっています。
  <br>

### 3. カスタム例外及び例外処理の設計

- 例外レスポンスの形式の統一や適切なHTTPステータスの返却により、フロントでのハンドリングを容易にしています。
- 必要に応じてカスタム例外にフィールド情報を含めて例外ハンドリングに活用することで、エラー原因を判定しやすくしています。
  <br>

</details>

## 今後の展望

<details>
<summary> アプリの展望を見る（クリックで表示） </summary>

### ユーザー操作性の向上

- モバイルアプリ化や長時間ログインを可能にする認証（JWT認証への切り替え）
- プロジェクトやタスクの検索・ソート機能の導入
- 見積時間の入力方法を改善し、hour 単位での扱いを可能にする
- 親子タスクの整合性を保つ自動調整機能（親子間の期限設定の整合性確保や進捗率の自動計算など）
- タイマーで計測した時間を自動で入力し、実績時間の記録を簡便に実施可能にする
  <br>

### セキュリティの向上

- ブルートフォース攻撃対策（ログイン試行回数の制限など）
- ログ監視体制の強化（ファイル出力による記録・分析の導入）
  <br>

### 機能拡張

- タスク情報を活用したスケジュール管理機能の追加、または外部カレンダーとの連携
- 個人タスクから、チーム・組織で利用できるタスク管理へと発展
  <br>

</details>

## 本アプリの実装での挑戦と学び

<details>
<summary> 挑戦と学びを見る（クリックで表示） </summary>
本アプリでは、初めてログイン機能の実装に挑戦しました。
Spring Security を用いたログイン機能の導入方法がわからなかったため、
公式ドキュメントや動画を参考に、実装・実行・修正を繰り返しながら実装を進めました。
<br>
導入後は、Postman での動作確認やテストコード作成時にも多くの問題に直面しました。
その際は AI ツールを活用して原因を分析し、認証を考慮した使い方や実装方法を調べながら問題解決に取り組みました。
この過程も時間を要しましたが、Spring Security の仕組みについての理解を深めることができました。
<br>
<br>
また、CSRF 攻撃や権限管理など、Webアプリにおけるセキュリティ対策の重要性も実感しました。  
現時点で対策は完全ではありませんが、今後も情報をキャッチアップし、安全なシステム構築に役立てたいと考えています。
<br>
<br>
今回は簡易的にフロントエンドを作成し、バックエンドとの連携も実施しました。 
フロント実装を通じて API の扱いづらさに気づき、フロント視点を意識した設計の重要性を学ぶ良い経験となりました。
今後はフロント学習も進め、より使いやすい API 設計を目指してスキルを広げていきたいと考えています。
</details>

## 終わりに

- 本アプリは学習用のポートフォリオですが、今後も更新をしていく予定です。
- 今後も技術への理解を深め、キャッチアップを続けながら、自身のスキルアップを目指します。
