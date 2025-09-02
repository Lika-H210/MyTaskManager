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
import com.portfolio.taskapp.MyTaskManager.exception.custom.NotUniqueException;
import com.portfolio.taskapp.MyTaskManager.exception.custom.RecordNotFoundException;
import com.portfolio.taskapp.MyTaskManager.user.mapper.UserAccountMapper;
import com.portfolio.taskapp.MyTaskManager.user.model.AccountRegisterRequest;
import com.portfolio.taskapp.MyTaskManager.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
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
  private static final String NEW_EMAIL = "new@example.com";
  private static final String PASSWORD_RAW = "rawPassword";
  private static final String PASSWORD_HASHED = "hashedPassword";
  private static final String NEW_PASSWORD_RAW = "newRawPassword";
  private static final String NEW_PASSWORD_HASHED = "newHashedPassword";

  @BeforeEach
  void setUp() {
    sut = new UserService(repository, passwordEncoder, mapper);
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  // アカウント情報取得：正常系
  @Test
  void アカウント情報取得時に適切なrepositoryとmapperが呼び出されていること() {
    UserAccount account = new UserAccount();

    when(repository.findAccountByPublicId(PUBLIC_ID)).thenReturn(account);

    sut.findAccount(PUBLIC_ID);

    verify(repository).findAccountByPublicId(PUBLIC_ID);
    verify(mapper).toUserAccountResponse(account);
  }

  // アカウント登録処理：正常系
  @Test
  void アカウント登録時に適切にrepositoryとencoderが呼び出されていること()
      throws NotUniqueException {
    AccountRegisterRequest request = new AccountRegisterRequest(null, EMAIL, PASSWORD_RAW);
    UserAccount registerAccount = new UserAccount();

    when(repository.existsByEmail(EMAIL)).thenReturn(false);
    when(passwordEncoder.encode(PASSWORD_RAW)).thenReturn(PASSWORD_HASHED);
    when(mapper.createRequestToUserAccount(eq(request), any(String.class), eq(PASSWORD_HASHED)))
        .thenReturn(registerAccount);

    sut.registerUser(request);

    verify(repository).existsByEmail(EMAIL);
    verify(passwordEncoder).encode(PASSWORD_RAW);
    verify(mapper).createRequestToUserAccount(eq(request), any(String.class), eq(PASSWORD_HASHED));
    verify(repository).registerUserAccount(registerAccount);
  }

  // アカウント登録処理：異常系(400 email重複チェック抵触)
  @Test
  void アカウント登録時のemail重複チェックがTRUEの場合に重複例外がThrowされ以降の処理が実行されないこと() {
    AccountRegisterRequest request = new AccountRegisterRequest(null, EMAIL, null);

    when(repository.existsByEmail(EMAIL)).thenReturn(true);

    assertThatThrownBy(() -> sut.registerUser(request))
        .isInstanceOf(NotUniqueException.class)
        .hasMessage("このメールアドレスは使用できません");

    verify(passwordEncoder, never()).encode(any());
  }

  // 認証情報更新メソッド：正常系（更新情報あり）
  @Test
  void 認証情報の更新処理でパスワードとemailが更新されていること() {
    // 準備
    UserAccount authAccount = UserAccount.builder()
        .publicId(PUBLIC_ID)
        .email(EMAIL)
        .password(PASSWORD_HASHED)
        .build();
    UserAccountDetails details = new UserAccountDetails(authAccount);
    UserAccount updateAccount = UserAccount.builder()
        .email(NEW_EMAIL)
        .password(NEW_PASSWORD_HASHED)
        .build();

    sut.refreshSecurityContext(details, updateAccount);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserAccountDetails actual = (UserAccountDetails) authentication.getPrincipal();

    assertThat(actual.getAccount().getPublicId()).isEqualTo(PUBLIC_ID);
    assertThat(actual.getUsername()).isEqualTo(NEW_EMAIL);
    assertThat(actual.getPassword()).isEqualTo(NEW_PASSWORD_HASHED);
    assertThat(authentication.getAuthorities()).isEmpty();
  }

  // 認証情報更新メソッド：正常系（更新情報なし）
  @Test
  void 認証情報の更新処理で更新情報がnullの場合は元の認証情報が維持されていること() {
    // 準備
    UserAccount authAccount = UserAccount.builder()
        .publicId(PUBLIC_ID)
        .email(EMAIL)
        .password(PASSWORD_HASHED)
        .build();
    UserAccountDetails details = new UserAccountDetails(authAccount);
    UserAccount updateAccount = UserAccount.builder()
        .email(null)
        .password(null)
        .build();

    sut.refreshSecurityContext(details, updateAccount);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserAccountDetails actual = (UserAccountDetails) authentication.getPrincipal();

    assertThat(actual.getAccount().getPublicId()).isEqualTo(PUBLIC_ID);
    assertThat(actual.getUsername()).isEqualTo(EMAIL);
    assertThat(actual.getPassword()).isEqualTo(PASSWORD_HASHED);
    assertThat(authentication.getAuthorities()).isEmpty();
  }

  @Test
  void 削除処理で適切なrepositoryを呼び出せていること() {
    when(repository.findAccountByPublicId(PUBLIC_ID)).thenReturn(new UserAccount());

    sut.deleteAccount(PUBLIC_ID);

    verify(repository).findAccountByPublicId(PUBLIC_ID);
    verify(repository).deleteAccount(PUBLIC_ID);
  }

  @Test
  void 削除処理を存在しないアカウントの公開IDで実行した場合例外かThrowされ以降の処理は実行されないこと() {
    when(repository.findAccountByPublicId(PUBLIC_ID)).thenReturn(null);

    assertThatThrownBy(() -> sut.deleteAccount(PUBLIC_ID))
        .isInstanceOf(RecordNotFoundException.class)
        .hasMessage("account not found");

    verify(repository, never()).deleteAccount(any());
  }
}
