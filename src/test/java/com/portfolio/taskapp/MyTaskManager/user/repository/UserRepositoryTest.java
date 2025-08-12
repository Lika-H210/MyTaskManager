package com.portfolio.taskapp.MyTaskManager.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;

@MybatisTest
class UserRepositoryTest {

  @Autowired
  private UserRepository sut;

  @Test
  void emailによる単独ユーザーのアカウント情報取得ができていること() {
    String email = "tanaka@example.com";

    UserAccount actual = sut.findAccountByEmail(email);

    assertThat(actual.getEmail()).isEqualTo(email);
    assertThat(actual.getPassword()).isNotNull();
    assertThat(actual.getPublicId()).isNotNull();
  }

  @Test
  void emailによるアカウント情報取得で削除扱いは取得できないこと() {
    String email = "sakujo@example.com";

    UserAccount actual = sut.findAccountByEmail(email);

    assertThat(actual).isNull();
  }

  // アカウント情報取得
  @Test
  void アカウント情報の取得で必要な情報のみが取得できていること() {
    String publicId = "5e8c0d2a-1234-4f99-a111-abcdef111111";

    UserAccount actual = sut.findAccountByPublicId(publicId);

    // 必須項目が含まれることの確認
    assertThat(actual).isNotNull();
    assertThat(actual.getPublicId()).isNotNull();
    assertThat(actual.getEmail()).isNotNull();
    assertThat(actual.getUserName()).isNotNull();
    assertThat(actual.getCreatedAt()).isNotNull();
    assertThat(actual.getUpdatedAt()).isNotNull();

    // 不要項目が含まれないことの確認
    assertThat(actual.getPassword()).isNull();
  }

  // アカウント登録処理
  @Test
  void アカウント登録で新たなユーザー情報が登録できていること() {
    String email = "user@example.com";
    UserAccount account = UserAccount.builder()
        .publicId("00000000-0000-0000-0000-000000000000")
        .userName("ユーザー名")
        .email(email)
        .password("passwordHash")
        .build();

    sut.registerUserAccount(account);

    UserAccount registeredAccount = sut.findAccountByEmail(email);

    // findAccountByEmailの取得Fieldのみ検証
    assertThat(registeredAccount)
        .usingRecursiveComparison()
        .comparingOnlyFields("publicId", "email", "password")
        .isEqualTo(account);
  }

  @Test
  void アカウントのprofile情報更新処理で更新対象項目が更新されていること() {
    String publicId = "5e8c0d2a-1234-4f99-a111-abcdef111111";
    String email = "test@mail.com";
    UserAccount account = UserAccount.builder()
        .publicId(publicId)
        .userName("テスト太郎")
        .email(email)
        .password("newHashedPassword")
        .build();

    sut.updateProfile(account);
    UserAccount actual = sut.findAccountByEmail(email);

    assertThat(actual)
        .usingRecursiveComparison()
        .comparingOnlyFields("publicId", "email", "password")
        .isEqualTo(account);
  }

  @Test
  void アカウントの削除処理実行によりアカウントが取得できなくなっていること() {
    String publicId = "5e8c0d2a-1234-4f99-a111-abcdef111111";

    sut.deleteAccount(publicId);
    UserAccount actual = sut.findAccountByPublicId(publicId);

    assertThat(actual).isNull();
  }

  // Emailの重複チェック
  @Test
  void emailが既に登録されている場合にTrueを返すこと() {
    boolean actual = sut.existsByEmail("tanaka@example.com");

    assertThat(actual).isTrue();
  }

  @Test
  void emailが未登録の場合にFalseを返すこと() {
    boolean actual = sut.existsByEmail("XXX@XXX.com");

    assertThat(actual).isFalse();
  }

  // 更新時Email重複チェック
  @Test
  void emailが自身の以外のレコードで登録されているの場合にTrueを返すこと() {
    String publicId = "5e8c0d2a-1234-4f99-a111-abcdef111111";
    String email = "sato@example.com";

    boolean actual = sut.existsByEmailExcludingUser(publicId, email);

    assertThat(actual).isTrue();
  }

  @Test
  void emailが自身の元のemailと同じの場合にFalseを返すこと() {
    String publicId = "5e8c0d2a-1234-4f99-a111-abcdef111111";
    String email = "taro@example.com";

    boolean actual = sut.existsByEmailExcludingUser(publicId, email);

    assertThat(actual).isFalse();
  }

  @Test
  void 未登録のemailでレコード検証した場合にFalseを返すこと() {
    String publicId = "5e8c0d2a-1234-4f99-a111-abcdef111111";
    String email = "XXX@XXX.com";

    boolean actual = sut.existsByEmailExcludingUser(publicId, email);

    assertThat(actual).isFalse();
  }
}