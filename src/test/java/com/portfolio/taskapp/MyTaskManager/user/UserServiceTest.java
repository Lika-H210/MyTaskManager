package com.portfolio.taskapp.MyTaskManager.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository repository;

  @Mock
  private PasswordEncoder passwordEncoder;

  private UserService sut;

  @BeforeEach
  void setUp() {
    sut = new UserService(repository, passwordEncoder);
  }

  @Test
  void アカウント登録時に適切にrepositoryとencoderが呼び出されていること() {
    String rawPassword = "rawPassword";
    UserAccount account = UserAccount.builder()
        .userName("ユーザー名")
        .email("user@example.com")
        .password(rawPassword)
        .build();
    String hashedPassword = "hashedPassword";
    when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);

    sut.registerUserAccount(account);

    verify(passwordEncoder).encode(rawPassword);
    verify(repository).registerUserAccount(account);

    assertThat(account.getPublicId()).isNotNull();
    assertThat(account.getPassword()).isEqualTo(hashedPassword);
  }


}