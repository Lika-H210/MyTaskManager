package com.portfolio.taskapp.MyTaskManager.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.portfolio.taskapp.MyTaskManager.auth.model.UserAccountDetails;
import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import com.portfolio.taskapp.MyTaskManager.exception.InvalidPasswordChangeException;
import com.portfolio.taskapp.MyTaskManager.exception.NotUniqueException;
import com.portfolio.taskapp.MyTaskManager.user.mapper.UserAccountMapper;
import com.portfolio.taskapp.MyTaskManager.user.model.AccountRegisterRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.AccountUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

  private static final String PUBLIC_ID = "00000000-0000-0000-0000-000000000000";
  private static final String EMAIL = "user@example.com";
  private static final String PASSWORD_RAW = "rawPassword";
  private static final String PASSWORD_HASHED = "hashedPassword";

  @BeforeEach
  void setUp() {
    sut = new UserService(repository, passwordEncoder, mapper);
  }

  @Test
  void アカウント情報取得時に適切なrepositoryとmapperが呼び出されていること() {
    UserAccount account = new UserAccount();

    when(repository.findAccountByPublicId(PUBLIC_ID)).thenReturn(account);

    sut.findAccount(PUBLIC_ID);

    verify(repository).findAccountByPublicId(PUBLIC_ID);
    verify(mapper).toUserAccountResponse(account);
  }

  @Test
  void アカウント登録時に適切にrepositoryとencoderが呼び出されていること()
      throws NotUniqueException {
    AccountRegisterRequest request = new AccountRegisterRequest(null, EMAIL,
        PASSWORD_RAW);
    UserAccount registerAccount = new UserAccount();

    when(repository.existsByEmail(EMAIL)).thenReturn(false);
    when(passwordEncoder.encode(PASSWORD_RAW)).thenReturn(PASSWORD_HASHED);
    when(mapper.CreateRequestToUserAccount(eq(request), any(String.class),
        eq(PASSWORD_HASHED)))
        .thenReturn(registerAccount);

    sut.registerUser(request);

    verify(repository).existsByEmail(EMAIL);
    verify(passwordEncoder).encode(PASSWORD_RAW);
    verify(mapper).CreateRequestToUserAccount(eq(request), any(), eq(PASSWORD_HASHED));
    verify(repository).registerUserAccount(registerAccount);
  }

  @Test
  void アカウント登録時のemail重複チェックがTRUEの場合に重複例外がThrowされ以降の処理が実行されないこと() {
    AccountRegisterRequest request = new AccountRegisterRequest(null, EMAIL, null);

    when(repository.existsByEmail(EMAIL)).thenReturn(true);

    assertThatThrownBy(() -> sut.registerUser(request))
        .isInstanceOf(NotUniqueException.class)
        .hasMessage("このメールアドレスは使用できません");

    verify(passwordEncoder, never()).encode(any());
  }

  @Test
  void アカウント情報更新において適切なmapperとrepositoryが呼び出され認証情報も更新されていること()
      throws NotUniqueException, InvalidPasswordChangeException {
    // 事前準備
    String newRawPassword = "newRowPassword";
    String newHashedPassword = "newHashedPassword";
    AccountUpdateRequest request = new AccountUpdateRequest(null, EMAIL, PASSWORD_RAW,
        newRawPassword);
    UserAccount authAccount = UserAccount.builder()
        .publicId(PUBLIC_ID)
        .password(PASSWORD_HASHED)
        .build();
    UserAccountDetails details = new UserAccountDetails(authAccount);
    UserAccount updateAccount = UserAccount.builder()
        .email(EMAIL)
        .password(newHashedPassword)
        .build();

    when(passwordEncoder.matches(PASSWORD_RAW, PASSWORD_HASHED)).thenReturn(true);
    when(passwordEncoder.encode(newRawPassword)).thenReturn(newHashedPassword);
    when(repository.existsByEmail(EMAIL)).thenReturn(false);
    when(mapper.updateRequestToUserAccount(request, PUBLIC_ID, newHashedPassword))
        .thenReturn(updateAccount);

    // SecurityContext をクリア
    SecurityContextHolder.clearContext();

    // 実行
    sut.updateAccount(details, request);

    // 検証: 処理工程の確認
    verify(passwordEncoder).matches(PASSWORD_RAW, PASSWORD_HASHED);
    verify(passwordEncoder).encode(newRawPassword);
    verify(repository).existsByEmail(EMAIL);
    verify(mapper).updateRequestToUserAccount(request, PUBLIC_ID, newHashedPassword);
    verify(repository).updateAccount(updateAccount);
    verify(mapper).toUserAccountResponse(updateAccount);

    // 検証: updateAuthInfoでの認証情報の更新確認
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication).isNotNull();
    assertThat(authentication.getPrincipal()).isInstanceOf(UserAccountDetails.class);

    UserAccountDetails updatedDetails = (UserAccountDetails) authentication.getPrincipal();
    assertThat(updatedDetails.getUsername()).isEqualTo(EMAIL);
    assertThat(updatedDetails.getPassword()).isEqualTo(newHashedPassword);
  }


  @Test
  void アカウント更新時にemail重複チェックがTRUEの場合に重複例外がThrowされ以降の処理が実行されないこと() {
    AccountUpdateRequest request = new AccountUpdateRequest(null, EMAIL, null, null);
    UserAccountDetails details = new UserAccountDetails(new UserAccount());

    when(repository.existsByEmail(EMAIL)).thenReturn(true);

    assertThatThrownBy(() -> sut.updateAccount(details, request))
        .isInstanceOf(NotUniqueException.class)
        .hasMessage("このメールアドレスは使用できません");
  }

}