package com.portfolio.taskapp.MyTaskManager.user.service;

import com.portfolio.taskapp.MyTaskManager.auth.details.UserAccountDetails;
import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import com.portfolio.taskapp.MyTaskManager.exception.custom.InvalidPasswordChangeException;
import com.portfolio.taskapp.MyTaskManager.exception.custom.NotUniqueException;
import com.portfolio.taskapp.MyTaskManager.exception.custom.RecordNotFoundException;
import com.portfolio.taskapp.MyTaskManager.user.dto.AccountRegisterRequest;
import com.portfolio.taskapp.MyTaskManager.user.dto.AccountResponse;
import com.portfolio.taskapp.MyTaskManager.user.dto.update.AccountEmailUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.dto.update.AccountPasswordUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.dto.update.AccountUserInfoUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.repository.UserRepository;
import com.portfolio.taskapp.MyTaskManager.user.service.mapper.UserAccountMapper;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ユーザーアカウントに関するビジネスロジックを提供するサービスクラス。
 * <p>
 * アカウント情報に関するCRUD処理を行います。必要に応じて認証情報の更新も行います。 <br> DBアクセスは UserRepository を介して行い、 レスポンスDTOはすべて
 * AccountResponse を利用します。
 */
@Service
public class UserService {

  private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final UserAccountMapper mapper;

  @Autowired
  public UserService(UserRepository repository,
      PasswordEncoder passwordEncoder,
      UserAccountMapper mapper) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
    this.mapper = mapper;
  }

  /**
   * 公開IDからユーザーアカウント情報を取得します。
   *
   * @param publicId 公開ID
   * @return アカウント情報
   */
  public AccountResponse findAccount(String publicId) {
    UserAccount account = repository.findAccountByPublicId(publicId);
    return mapper.toUserAccountResponse(account);
  }

  /**
   * 新規ユーザーアカウントを登録します。登録処理のみを行い、レスポンスは返しません。
   *
   * @param request アカウント登録リクエスト
   * @throws NotUniqueException 入力されたメールアドレスが既に使用されている場合
   */
  @Transactional
  public void registerUser(AccountRegisterRequest request) throws NotUniqueException {
    validateEmailUniqueness(request.getEmail());

    String publicId = UUID.randomUUID().toString();
    String hashedPassword = passwordEncoder.encode(request.getPassword());
    UserAccount registerAccount = mapper.createRequestToUserAccount(request, publicId,
        hashedPassword);

    repository.registerUserAccount(registerAccount);
  }

  /**
   * ユーザーの基本情報を更新します。
   *
   * @param userDetails 現在認証済みのユーザー情報
   * @param request     ユーザー情報の更新内容
   * @return 公開IDと更新後の基本情報を含むアカウント情報
   */
  @Transactional
  public AccountResponse updateUserInfo(UserAccountDetails userDetails,
      AccountUserInfoUpdateRequest request) {
    UserAccount account = mapper
        .updateUserInfoRequestToUserAccount(request, userDetails.getAccount().getPublicId());
    repository.updateAccount(account);

    return mapper.toUserAccountResponse(account);
  }

  /**
   * ユーザーのメールアドレスを更新します。
   * <p>
   * メールアドレスが変更された場合はセキュリティコンテキストも更新します。
   *
   * @param userDetails 現在認証済みのユーザー情報
   * @param request     メールアドレス更新リクエスト
   * @return 公開IDと更新後のメールアドレスを含むアカウント情報
   * @throws NotUniqueException 更新メールアドレスが既に他ユーザーに使用されている場合
   */
  @Transactional
  public AccountResponse updateEmail(UserAccountDetails userDetails,
      AccountEmailUpdateRequest request) throws NotUniqueException {

    if (request.getEmail().equals(userDetails.getUsername())) {
      return mapper.toUserAccountResponse(userDetails.getAccount());
    }
    validateEmailUniqueness(request.getEmail());

    UserAccount account = mapper
        .updateEmailRequestToUserAccount(request, userDetails.getAccount().getPublicId());
    repository.updateAccount(account);

    refreshSecurityContext(userDetails, account);

    return mapper.toUserAccountResponse(account);
  }

  /**
   * ユーザーのパスワードを更新します。
   * <p>
   * 現在のパスワードが一致しない場合はエラーを返します。 更新後はセキュリティコンテキストを更新します。
   *
   * @param userDetails 現在認証済みのユーザー情報
   * @param request     パスワードの更新リクエスト
   * @return 公開IDのみを含むアカウント情報。（セキュリティ上の理由により更新されたパスワード情報は含みません）
   * @throws InvalidPasswordChangeException 現在のパスワードが一致しない場合
   */
  @Transactional
  public AccountResponse updatePassword(UserAccountDetails userDetails,
      AccountPasswordUpdateRequest request) throws InvalidPasswordChangeException {

    if (!passwordEncoder.matches(request.getCurrentPassword(), userDetails.getPassword())) {
      throw new InvalidPasswordChangeException("currentPassword",
          "現在のパスワードをご確認ください");
    }

    String newHashedPassword = passwordEncoder.encode(request.getNewPassword());

    UserAccount account = mapper
        .updatePasswordRequestToUserAccount(newHashedPassword,
            userDetails.getAccount().getPublicId());
    repository.updateAccount(account);

    refreshSecurityContext(userDetails, account);

    return mapper.toUserAccountResponse(account);
  }

  /**
   * ユーザーアカウントを削除します。
   *
   * @param publicId 公開ID
   * @throws RecordNotFoundException 指定されたユーザーが存在しない場合
   */
  @Transactional
  public void deleteAccount(String publicId) {
    if (repository.findAccountByPublicId(publicId) == null) {
      throw new RecordNotFoundException("account not found");
    }
    repository.deleteAccount(publicId);
  }

  // ──────────────── Private methods ────────────────

  /**
   * メールアドレスがユニークか検証します。登録された全メールアドレスを検証対象とします。
   *
   * @param requestEmail 検証対象のメールアドレス
   * @throws NotUniqueException メールアドレスが既に使用されている場合
   */
  private void validateEmailUniqueness(String requestEmail)
      throws NotUniqueException {
    if (repository.existsByEmail(requestEmail)) {
      throw new NotUniqueException("email", "このメールアドレスは使用できません");
    }
  }

  /**
   * 更新されたアカウント情報を基に、新しい AuthenticationToken を生成してセキュリティコンテキストに設定します
   *
   * @param userDetails   現在認証済みのユーザー情報
   * @param updateAccount 更新後のアカウント情報
   */
  void refreshSecurityContext(UserAccountDetails userDetails, UserAccount updateAccount) {
    UserAccount updateUserAccount = UserAccount.builder()
        .publicId(userDetails.getAccount().getPublicId())
        .email(Optional.ofNullable(updateAccount.getEmail())
            .orElse(userDetails.getUsername()))
        .password(Optional.ofNullable(updateAccount.getPassword())
            .orElse(userDetails.getPassword()))
        .build();
    UserAccountDetails updatedUserDetails = new UserAccountDetails(updateUserAccount);

    UsernamePasswordAuthenticationToken newAuth =
        new UsernamePasswordAuthenticationToken(
            updatedUserDetails,
            updatedUserDetails.getPassword(),
            userDetails.getAuthorities());

    SecurityContextHolder.getContext().setAuthentication(newAuth);
  }

}
