package com.portfolio.taskapp.MyTaskManager.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.portfolio.taskapp.MyTaskManager.auth.details.UserAccountDetails;
import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import com.portfolio.taskapp.MyTaskManager.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserAccountDetailsServiceTest {

  @Mock
  private UserRepository repository;

  private UserAccountDetailsService sut;

  @BeforeEach
  void setUp() {
    sut = new UserAccountDetailsService(repository);
  }

  @Test
  void 存在するメールアドレスが入力された場合にアカウント詳細が返ること() {
    String email = "email@example.com";
    UserAccount account = UserAccount.builder()
        .email(email)
        .password("password")
        .build();

    when(repository.findAccountByEmail(email)).thenReturn(account);

    UserDetails actual = sut.loadUserByUsername(email);
    UserAccountDetails accountDetails = (UserAccountDetails) actual;

    verify(repository).findAccountByEmail(email);
    assertThat(accountDetails.getAccount()).isEqualTo(account);
  }

  @Test
  void 存在しないメールアドレスが入力された場合に例外がThrowされること() {
    String email = "email@example.com";

    when(repository.findAccountByEmail(email)).thenReturn(null);

    assertThatThrownBy(() -> sut.loadUserByUsername(email))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessageContaining("Login failed.");
  }

  @Test
  void メールアドレスがnullでリクエストされた場合に例外がThrowされること() {
    String email = null;

    assertThatThrownBy(() -> sut.loadUserByUsername(email))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessageContaining("Login failed.");

    verify(repository, never()).findAccountByEmail(any());
  }

  @Test
  void メールアドレスが空でリクエストされた場合に例外がThrowされること() {
    String email = "";

    assertThatThrownBy(() -> sut.loadUserByUsername(email))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessageContaining("Login failed.");
  }

}
