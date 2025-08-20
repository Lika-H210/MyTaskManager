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
import com.portfolio.taskapp.MyTaskManager.user.model.AccountResponse;
import com.portfolio.taskapp.MyTaskManager.user.model.AccountUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    when(mapper.CreateRequestToUserAccount(eq(request), any(String.class), eq(PASSWORD_HASHED)))
        .thenReturn(registerAccount);

    sut.registerUser(request);

    verify(repository).existsByEmail(EMAIL);
    verify(passwordEncoder).encode(PASSWORD_RAW);
    verify(mapper).CreateRequestToUserAccount(eq(request), any(String.class), eq(PASSWORD_HASHED));
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

  // アカウント更新処理：正常系
  @Test
  void アカウント情報更新において適切なmapperとrepositoryが呼び出され認証情報も更新されていること()
      throws NotUniqueException, InvalidPasswordChangeException {
    // 事前準備
    AccountUpdateRequest request = new AccountUpdateRequest(null, NEW_EMAIL, PASSWORD_RAW,
        NEW_PASSWORD_RAW);
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

    when(passwordEncoder.matches(PASSWORD_RAW, PASSWORD_HASHED)).thenReturn(true);
    when(passwordEncoder.encode(NEW_PASSWORD_RAW)).thenReturn(NEW_PASSWORD_HASHED);
    when(repository.existsByEmail(NEW_EMAIL)).thenReturn(false);
    when(mapper.updateRequestToUserAccount(request, PUBLIC_ID, NEW_PASSWORD_HASHED))
        .thenReturn(updateAccount);

    // SecurityContext をクリア
    SecurityContextHolder.clearContext();

    // 実行
    sut.updateAccount(details, request);

    // 検証
    // パスワード検証メソッドの間接呼び出し確認
    verify(passwordEncoder).matches(PASSWORD_RAW, PASSWORD_HASHED);
    // email重複チェックメソッドの間接呼び出し確認
    verify(repository).existsByEmail(NEW_EMAIL);
    verify(mapper).updateRequestToUserAccount(request, PUBLIC_ID, NEW_PASSWORD_HASHED);
    verify(repository).updateAccount(updateAccount);
    verify(mapper).toUserAccountResponse(updateAccount);
    // 認証情報更新メソッドの間接呼び出し確認
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
  }

  @Test
  void アカウント更新時に全項目nullの場合に早期リターンすること()
      throws NotUniqueException, InvalidPasswordChangeException {
    AccountUpdateRequest request = new AccountUpdateRequest(null, null, null, null);
    UserAccountDetails details = new UserAccountDetails(new UserAccount());

    AccountResponse actual = sut.updateAccount(details, request);

    assertThat(actual).usingRecursiveComparison()
        .isEqualTo(new AccountResponse());
  }

  @Test
  void アカウント更新で自身の登録済みEmailで更新した場合に重複チェックが実行されないこと()
      throws NotUniqueException, InvalidPasswordChangeException {
    AccountUpdateRequest request = new AccountUpdateRequest(null, EMAIL, null, null);
    UserAccount authAccount = UserAccount.builder()
        .publicId(PUBLIC_ID)
        .email(EMAIL)
        .build();
    UserAccountDetails details = new UserAccountDetails(authAccount);
    UserAccount updateAccount = new UserAccount();

    when(mapper.updateRequestToUserAccount(request, PUBLIC_ID, null))
        .thenReturn(updateAccount);

    sut.updateAccount(details, request);

    verify(repository, never()).existsByEmail(any());
  }

}
