package com.portfolio.taskapp.MyTaskManager.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import com.portfolio.taskapp.MyTaskManager.user.mapper.UserAccountMapper;
import com.portfolio.taskapp.MyTaskManager.user.model.UserAccountRequest;
import com.portfolio.taskapp.MyTaskManager.user.repository.UserRepository;
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

  @Mock
  private UserAccountMapper mapper;

  private UserService sut;

  @BeforeEach
  void setUp() {
    sut = new UserService(repository, passwordEncoder, mapper);
  }

  @Test
  void アカウント登録時に適切にrepositoryとencoderが呼び出されていること() {
    String publicId = "00000000-0000-0000-0000-000000000000";
    String userName = "ユーザー名";
    String email = "user@example.com";
    String rawPassword = "rawPassword";
    UserAccountRequest account = new UserAccountRequest(userName, email, rawPassword);
    UserAccount registerAccount = UserAccount.builder()
        .publicId(publicId)
        .userName(userName)
        .email(email)
        .password(rawPassword)
        .build();

    String hashedPassword = "hashedPassword";
    when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);
    when(mapper.toUserAccount(eq(account), any())).thenReturn(registerAccount);

    sut.registerUserAccount(account);

    verify(passwordEncoder).encode(rawPassword);
    verify(repository).registerUserAccount(registerAccount);

    assertThat(account.getPassword()).isEqualTo(hashedPassword);
  }

}