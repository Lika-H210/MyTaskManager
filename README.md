# MyTaskManager

## はじめに

本リポジトリは、個人ユーザー向けタスク管理アプリのバックエンド実装です。

本リポジトリのコードは学習・ポートフォリオ目的で提供しており、  
利用に関するトラブル等については責任を負いかねますのでご了承ください。

## 背景

個人のタスク管理は業務を的確に遂行するうえで重要ですが、細かいタスクを追いかけるのは手間がかかります。
<br>企業で導入されている業務管理システムは、プロジェクト管理や進捗報告を目的としており、個人のタスク実行という視点では過剰な情報を求められることがあります。
例えば、詳細なガントチャート、複雑なステータス管理、様々なカテゴライズの追加など、「タスクを実行する」という本来の目的以外の情報入力に時間を取られてしまいます。
<br>一方、既存の個人向けタスク管理アプリは、シンプルさを重視しすぎて、業務で必要な「プロジェクト単位での管理」や「親子タスクの関係性」が扱いにくいと感じていました。
<br>そこで本アプリでは、この2つの中間を目指し、業務タスクの実行に必要な機能に絞りつつ、実業務の時間確保と継続的なタスク管理の両立を目指して開発を行いました。
<br>
<br>

## 概要

本リポジトリは、個人ユーザー向けタスク管理アプリとして、
タスクやプロジェクトの管理、ユーザー認証、DB 連携などを行うAPIを提供します。  
<br>
業務タスクの管理に必要十分な機能に絞り、以下を重視した設計としています：

- プロジェクト単位でのタスク整理
- 親子タスクによる階層的な管理
- タスク実行に必要な情報（期限、進捗、見積時間）のみに絞った入力項目

※補足：デモ用の簡易フロントも同梱していますが、フロントは主に生成AIを利用して作成しています、その点ご承知おきください。
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

## 実装機能の概要

- ユーザー認証（ログイン/ログアウト）
- アカウント情報管理（取得 / 登録 / 更新 / 削除）
- プロジェクト管理（取得 / 登録 / 更新 / 削除）
- タスク管理（取得 / 登録 / 更新 / 削除）

※補足：各処理はログイン中のユーザーに紐づく情報に対してのみ正常に処理されます。（アカウント登録は除く）
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
ポート 8080（Spring Boot）および 3306（MySQL）が他のプロセスで使用されていないことを確認してください。  
もし既に使用されている場合は、application.properties 内でポート番号を変更して起動してください。

### 5. 動作確認

- ブラウザからの動作確認：http://localhost:8080/login.html

    - 下記デモユーザー情報でのログインも可能です。
        - Email: demo@example.com
        - パスワード: demo_password  
          <br>
- API仕様書の確認：http://localhost:8080/swagger-ui/index.html

</details> 

## 動作イメージ

### タスク管理

#### ログイン → 各種一覧取得

https://github.com/user-attachments/assets/d42a9913-9417-4fdf-89a5-f1ec745a0c25

#### プロジェクトの登録・更新・削除

https://github.com/user-attachments/assets/6d23ceee-7fcf-44c5-aa20-aadaa3534ffc

#### 親タスクの登録・更新・削除

https://github.com/user-attachments/assets/a7782682-5620-429e-8c1d-b882019a76f4

### アカウント情報

#### アカウント登録

https://github.com/user-attachments/assets/dc03b39c-7189-4070-818d-20423eb3bde3

#### プロジェクト一覧　→　アカウント一覧 → メールアドレス更新

https://github.com/user-attachments/assets/ca24a4c9-864c-4919-b1cc-bb41c9e1b75e

### その他

#### バリデーション

<img width="686" height="231" alt="image" src="https://github.com/user-attachments/assets/12367e19-8bda-4e5b-b36c-2e92cc6c2d91" />
<br>
400エラーのレスポンス情報を基にエラーメッセージを表示します。
<br>

## API 概要

### Auth

| メソッド | パス          | 説明         |
|------|-------------|------------|
| GET  | /csrf-token | CSRFトークン取得 |

### User

| メソッド   | パス                 | 説明                |
|--------|--------------------|-------------------|
| GET    | /users/me          | ログインユーザー情報取得      |
| POST   | /users/register    | 新規ユーザー登録          |
| PUT    | /users/me/info     | ユーザー情報更新          |
| PUT    | /users/me/email    | メールアドレス更新         |
| PUT    | /users/me/password | パスワード更新           |
| DELETE | /users/me          | ユーザーアカウント削除（論理削除） |

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
| GET    | /task-trees/{taskId}             | 単独親子タスク取得   |
| GET    | /tasks/{taskId}                  | 単体タスク取得     |
| POST   | /projects/{projectId}/tasks      | 親タスク登録      |
| POST   | /tasks/{taskId}/subtasks         | 子タスク登録      |
| PUT    | /tasks/{taskId}                  | タスク更新       |
| DELETE | /tasks/{taskId}                  | タスク削除（論理削除） |

## ER図

<img width="351" height="497" alt="mKHFjqrNbX" src="https://github.com/user-attachments/assets/9b14d7c5-0d56-4aee-abdd-f658e7dc06d5" />
<br>

## セキュリティ対策

本アプリケーションでは、以下のセキュリティ対策を実装しています。

### 認証・認可

- セッションベース認証: Spring Securityによる実装
- 認可チェック: 認証ユーザーのIDとリソースのユーザーIDを照合し、所有権を確認
- 不正アクセス検知: 他ユーザーのリソースへのアクセス試行を403エラーとして処理し、デバッグ用にログ出力（将来的には運用ログへの拡張を想定）

### CSRF対策

- Spring SecurityによるCSRFトークンの自動生成・検証
- トークン取得用エンドポイント（`/csrf-token`）を実装
- アカウント登録など、認証不要エンドポイントはCSRF保護から除外

### その他

- SQLインジェクション: MyBatisのプリペアドステートメント（`#{}`）を使用
- パスワードセキュリティ: BCryptPasswordEncoderによるハッシュ化
- セッションタイムアウト: 30分の非アクティブでセッション無効化
  <br>

## 動作確認環境

- **OS**: Windows 11
- **JDK**: 21
- **ビルドツール**: Gradle 8.14.3
- **ブラウザ**: Chrome 140.x
  <br>

## 自動テスト

- 単体テスト：
    - Controller、Service、Repository、Converter の単体テスト<br>
      ※Controllerは認証不要APIのみ対象
    - 各リクエストDTOのバリデーションテスト（ProjectRequestTest, TaskRequestTest 等）<br>
      <br>
- 結合テスト（Controller + SecurityConfig + 例外ハンドリング）：
    - MockMvc を用いて Controller の各エンドポイントを呼び出し、<br>
      Service との連携、認証・CSRF、バリデーション、例外ハンドリングの挙動を確認
    - Service はモック化しており、DBアクセスは含みません
      <br>

## 実装のポイント

### 1. ログイン機能の実装

- 本アプリでは、ユーザー固有のタスク管理を行うため、ログインによる本人確認を導入し、<br>
  認証済みユーザーのみ自身のデータへアクセス可能としています。
- ログインの実装は容易さを重視し **セッションベースの認証方式** を採用しています。<br>
- Spring Securityの標準機能を活用することで、実装の信頼性を確保しています。
  <br>

### 2. 親子関係をもつ階層構造テーブル設計

- タスクテーブルは階層構造を持つ設計になっており、一つのテーブルでタスク管理をしています。<br>
  これにより、DB変更時の影響範囲を限定できます。

- 現状は親ID方式で 2 階層の実装ですが、3 階層程度までは再帰処理にすることで拡張可能です。<br>
  より深い階層が必要な場合は、経路列挙方式などに切り替えて、階層構造を効率的に管理することを想定しています。
  <br>

### 3. カスタム例外及び例外処理の設計

- すべての例外処理はレスポンスを {status, error, detail} の形式に統一しています。<br>
  detail の内容はケースごとに以下のように設計しています：<br>
    - フィールド単位のバリデーションエラー： {field: message} の Map
    - フィールドに紐づかないエラー： message
- カスタム例外は、必要に応じて field 名を情報に含めるよう実装しています。
- エラースポンスの形式を統一することでフロントでのハンドリングが容易になるように配慮しました。
  <br>

### 4. データ返却設計の方針

用途に応じて「DTO返却」と「Entity返却」を使い分ける方針としています。

- **ユーザーアカウント**
    - セキュリティを重視し、公開可能なfieldのみ含むDTOを返却しています。
- **タスク / プロジェクト**
    - センシティブ情報が少ないため、Entity を返却しています。
    - 内部 ID 等返却すべきでない情報は `@JsonIgnore` で除外しています。
      <br>

## 今後の展望

### ユーザー操作性の向上

- モバイルアプリ化や長時間ログインを可能にする認証（JWT認証への切り替え）
- プロジェクトやタスクの検索やソート機能の導入
- 見積時間の入力方法を改善し、hour 単位での扱いを可能にする
- 子タスクの見積時間と進捗率をもとに、親タスクの進捗率を自動計算
- 子タスクの期限を親タスクの期限内にのみ設定できる仕組みを導入
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
  <br>

## 本アプリの実装での挑戦と学び

本アプリでは、初めて Spring Security を導入し、ログイン機能を実装しました。
認証情報の管理や設定ファイルの構成など、初めての要素が多く実装には時間を要しましたが、公式ドキュメントや動画を参考にしながら、まずは最低限動作する形を構築し、そこから徐々に理解を深めながら機能を拡張していきました。
<br>
ログイン機能の導入後は、Postman での動作確認やテストコードの作成でも認証を考慮する必要があり、多くの問題に直面しました。
しかし、AIツールを活用しながら原因を分析し、一つずつ解決することができ、その中でSpring Security
の仕組みも実践的に学ぶことができました。
<br>
<br>
また、CSRF 攻撃や権限管理など、Webアプリにおけるセキュリティ対策の重要性も強く実感しました。  
現時点で対策は完全ではありませんが、今後も情報をキャッチアップしながら、安全なシステムを構築できるよう学びを続けていきたいと考えています。
<br>
<br>
さらに、今回は簡易的にフロントエンドを作成し、バックエンドとの連携も実施しました。  
フロントの実装を通して、API連携時に扱いづらさを感じる部分もありましたが、フロントの視点を意識した設計の大切さを学ぶ良い経験となりました。
今後はフロントエンドの学習にも取り組み、より使いやすい API 設計を実現できるようスキルを広げていきたいと考えています。
