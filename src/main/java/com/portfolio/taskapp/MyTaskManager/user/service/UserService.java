package com.portfolio.taskapp.MyTaskManager.user.service;

import com.portfolio.taskapp.MyTaskManager.auth.model.UserAccountDetails;
import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import com.portfolio.taskapp.MyTaskManager.exception.custom.InvalidPasswordChangeException;
import com.portfolio.taskapp.MyTaskManager.exception.custom.NotUniqueException;
import com.portfolio.taskapp.MyTaskManager.exception.custom.RecordNotFoundException;
import com.portfolio.taskapp.MyTaskManager.user.mapper.UserAccountMapper;
import com.portfolio.taskapp.MyTaskManager.user.model.AccountRegisterRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.AccountResponse;
import com.portfolio.taskapp.MyTaskManager.user.model.AccountUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.update.AccountEmailUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.update.AccountUserInfoUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private UserRepository repository;
  private PasswordEncoder passwordEncoder;
  private UserAccountMapper mapper;

  @Autowired
  public UserService(UserRepository repository,
      PasswordEncoder passwordEncoder,
      UserAccountMapper mapper) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
    this.mapper = mapper;
  }

  public AccountResponse findAccount(String publicId) {
    UserAccount account = repository.findAccountByPublicId(publicId);
    return mapper.toUserAccountResponse(account);
  }

  @Transactional
  public void registerUser(AccountRegisterRequest request) throws NotUniqueException {
    validateEmailUniqueness(request.getEmail());

    String publicId = UUID.randomUUID().toString();
    String hashedPassword = passwordEncoder.encode(request.getPassword());
    UserAccount registerAccount = mapper.createRequestToUserAccount(request, publicId,
        hashedPassword);

    repository.registerUserAccount(registerAccount);
  }

  @Transactional
  public AccountResponse updateUserInfo(UserAccountDetails userDetails,
      AccountUserInfoUpdateRequest request) {
    UserAccount account = mapper
        .updateUserInfoRequestToUserAccount(request, userDetails.getAccount().getPublicId());
    repository.updateAccount(account);

    return mapper.toUserAccountResponse(account);
  }

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

  @Transactional
  public AccountResponse updateAccount(UserAccountDetails userDetails,
      AccountUpdateRequest request)
      throws NotUniqueException, InvalidPasswordChangeException {

    String publicId = userDetails.getAccount().getPublicId();

    // 更新情報がない場合
    if (ObjectUtils.allNull(request.getUserName(), request.getEmail(),
        request.getCurrentPassword(), request.getNewPassword())) {
      return new AccountResponse();
    }

    String hashedPassword = preparePasswordForUpdate(userDetails, request);

    if (request.getEmail() != null && !request.getEmail().equals(userDetails.getUsername())) {
      validateEmailUniqueness(request.getEmail());
    }

    UserAccount updateAccount = mapper.updateRequestToUserAccount(request, publicId,
        hashedPassword);
    repository.updateAccount(updateAccount);

    // 認証情報に変更がない場合は処理終了。変更ありは認証情報を更新。
    if (request.getEmail() != null || hashedPassword != null) {
      refreshSecurityContext(userDetails, updateAccount);
    }

    return mapper.toUserAccountResponse(updateAccount);
  }

  @Transactional
  public void deleteAccount(String publicId) {
    if (repository.findAccountByPublicId(publicId) == null) {
      throw new RecordNotFoundException("account not found");
    }
    repository.deleteAccount(publicId);
  }

  // ──────────────── Private methods ────────────────

  private void validateEmailUniqueness(String requestEmail)
      throws NotUniqueException {
    if (repository.existsByEmail(requestEmail)) {
      throw new NotUniqueException("email", "このメールアドレスは使用できません");
    }
  }

  String preparePasswordForUpdate(UserAccountDetails userDetails,
      AccountUpdateRequest request)
      throws InvalidPasswordChangeException {
    // 現・新パスワードの入力状態: 0=両方なし, 1=どちらかのみ, 2=両方あり
    int state = (request.getCurrentPassword() == null ? 0 : 1)
        + (request.getNewPassword() == null ? 0 : 1);

    return switch (state) {
      case 1 -> throw new InvalidPasswordChangeException(
          "パスワード変更には現在のパスワードと新しいパスワードの両方が必要です");
      case 2 -> {
        if (!passwordEncoder.matches(request.getCurrentPassword(), userDetails.getPassword())) {
          throw new InvalidPasswordChangeException("現在のパスワードをご確認ください");
        }
        yield passwordEncoder.encode(request.getNewPassword());
      }
      default -> null;
    };
  }

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
