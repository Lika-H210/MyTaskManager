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
  void publicIdによる単独ユーザーのアカウント情報取得ができていること() {
    String email = "tanaka@example.com";

    UserAccount actual = sut.findAccountByEmail(email);

    assertThat(actual.getEmail()).isEqualTo(email);
    assertThat(actual.getPassword()).isNotNull();
    assertThat(actual.getPublicId()).isNotNull();
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
}