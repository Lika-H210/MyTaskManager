package com.portfolio.taskapp.MyTaskManager.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.portfolio.taskapp.MyTaskManager.auth.model.UserAccountDetails;
import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import com.portfolio.taskapp.MyTaskManager.exception.InvalidPasswordChangeException;
import com.portfolio.taskapp.MyTaskManager.exception.NotUniqueException;
import com.portfolio.taskapp.MyTaskManager.user.mapper.UserAccountMapper;
import com.portfolio.taskapp.MyTaskManager.user.model.ProfileUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.UserAccountCreateRequest;
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

  @BeforeEach
  void setUp() {
    sut = new UserService(repository, passwordEncoder, mapper);
  }

  @Test
  void アカウント情報取得時に適切なrepositoryとmapperが呼び出されていること() {
    String publicId = "00000000-0000-0000-0000-000000000000";
    UserAccount account = new UserAccount();

    when(repository.findAccountByPublicId(publicId)).thenReturn(account);

    sut.findAccount(publicId);

    verify(repository).findAccountByPublicId(publicId);
    verify(mapper).toUserAccountResponse(account);
  }

  @Test
  void アカウント登録時に適切にrepositoryとencoderが呼び出されていること()
      throws NotUniqueException {
    String publicId = "00000000-0000-0000-0000-000000000000";
    String userName = "ユーザー名";
    String email = "user@example.com";
    String rawPassword = "rawPassword";
    String hashedPassword = "hashedPassword";
    UserAccountCreateRequest request = new UserAccountCreateRequest(userName, email, rawPassword);
    UserAccount registerAccount = UserAccount.builder()
        .publicId(publicId)
        .userName(userName)
        .email(email)
        .password(hashedPassword)
        .build();

    when(repository.existsByEmail(email)).thenReturn(false);
    when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);
    when(mapper.CreateRequestToUserAccount(eq(request), any(String.class), eq(hashedPassword)))
        .thenReturn(registerAccount);

    sut.registerUser(request);

    verify(repository).existsByEmail(email);
    verify(passwordEncoder).encode(rawPassword);
    verify(mapper).CreateRequestToUserAccount(eq(request), any(), eq(hashedPassword));
    verify(repository).registerUserAccount(registerAccount);
  }

  @Test
  void アカウント登録時のemail重複チェックがTRUEの場合に重複例外がThrowされ以降の処理が実行されないこと() {
    String userName = "ユーザー名";
    String email = "user@example.com";
    String rawPassword = "rawPassword";
    UserAccountCreateRequest request = new UserAccountCreateRequest(userName, email, rawPassword);

    when(repository.existsByEmail(email)).thenReturn(true);

    NotUniqueException thrown = assertThrows(
        NotUniqueException.class,
        () -> sut.registerUser(request)
    );

    assertThat(thrown.getMessage()).isEqualTo("このメールアドレスは使用できません");
    verify(passwordEncoder, never()).encode(anyString());
  }

  @Test
  void アカウントのprofile情報更新において適切なmapperとrepositoryが呼び出されていること()
      throws NotUniqueException, InvalidPasswordChangeException {
    // 事前準備
    String publicId = "00000000-0000-0000-0000-000000000000";
    String oldEmail = "Old@example.com";
    String newEmail = "New@example.com";
    String oldRawPassword = "oldRowPassword";
    String newRawPassword = "newRowPassword";
    String oldHashPassword = "oldHashPassword";
    String newHashPassword = "newHashPassword";
    ProfileUpdateRequest request = new ProfileUpdateRequest("name", newEmail, oldRawPassword,
        newRawPassword);
    UserAccount authAccount = UserAccount.builder()
        .publicId(publicId)
        .email(oldEmail)
        .password(oldHashPassword)
        .build();
    UserAccountDetails details = new UserAccountDetails(authAccount);
    UserAccount updateAccount = UserAccount.builder()
        .publicId(publicId)
        .email(newEmail)
        .password(newHashPassword)
        .build();

    when(passwordEncoder.matches(oldRawPassword, oldHashPassword)).thenReturn(true);
    when(passwordEncoder.encode(newRawPassword)).thenReturn(newHashPassword);
    when(repository.existsByEmailExcludingUser(publicId, newEmail)).thenReturn(false);
    when(mapper.profileToUserAccount(request, publicId, newHashPassword)).thenReturn(updateAccount);

    // SecurityContext をクリア
    SecurityContextHolder.clearContext();

    // 実行
    sut.updateProfile(details, request);

    // 検証: 処理工程の確認
    verify(passwordEncoder).matches(oldRawPassword, oldHashPassword);
    verify(passwordEncoder).encode(newRawPassword);
    verify(repository).existsByEmailExcludingUser(publicId, newEmail);
    verify(mapper).profileToUserAccount(request, publicId, newHashPassword);
    verify(repository).updateProfile(updateAccount);
    verify(mapper).toUserAccountResponse(updateAccount);

    // 検証: updateAuthInfoでの認証情報の更新確認
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication).isNotNull();
    assertThat(authentication.getPrincipal()).isInstanceOf(UserAccountDetails.class);

    UserAccountDetails updatedDetails = (UserAccountDetails) authentication.getPrincipal();
    assertThat(updatedDetails.getUsername()).isEqualTo(newEmail);
    assertThat(updatedDetails.getPassword()).isEqualTo(newHashPassword);
  }


  @Test
  void アカウント更新時にemail重複チェックがTRUEの場合に重複例外がThrowされ以降の処理が実行されないこと()
      throws NotUniqueException {
    String publicId = "00000000-0000-0000-0000-000000000000";
    String email = "user@example.com";
    ProfileUpdateRequest request = new ProfileUpdateRequest(null, email, null, null);
    UserAccount authAccount = UserAccount.builder()
        .publicId(publicId)
        .build();
    UserAccountDetails details = new UserAccountDetails(authAccount);

    when(repository.existsByEmailExcludingUser(publicId, email)).thenReturn(true);

    NotUniqueException thrown = assertThrows(NotUniqueException.class,
        () -> sut.updateProfile(details, request));

    assertThat(thrown.getMessage()).isEqualTo("このメールアドレスは使用できません");
  }

}