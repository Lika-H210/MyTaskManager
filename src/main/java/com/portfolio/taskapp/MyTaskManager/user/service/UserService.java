package com.portfolio.taskapp.MyTaskManager.user.service;

import com.portfolio.taskapp.MyTaskManager.auth.model.UserAccountDetails;
import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import com.portfolio.taskapp.MyTaskManager.exception.InvalidPasswordChangeException;
import com.portfolio.taskapp.MyTaskManager.exception.NotUniqueException;
import com.portfolio.taskapp.MyTaskManager.exception.RecordNotFoundException;
import com.portfolio.taskapp.MyTaskManager.user.mapper.UserAccountMapper;
import com.portfolio.taskapp.MyTaskManager.user.model.AccountUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.UserAccountCreateRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.UserAccountResponse;
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

  public UserAccountResponse findAccount(String publicId) {
    UserAccount account = repository.findAccountByPublicId(publicId);
    return mapper.toUserAccountResponse(account);
  }

  @Transactional
  public void registerUser(UserAccountCreateRequest request) throws NotUniqueException {
    // email重複チェック
    if (repository.existsByEmail(request.getEmail())) {
      throw new NotUniqueException("email", "このメールアドレスは使用できません");
    }

    String publicId = UUID.randomUUID().toString();
    String hashedPassword = passwordEncoder.encode(request.getPassword());
    UserAccount registerAccount = mapper.CreateRequestToUserAccount(request, publicId,
        hashedPassword);

    repository.registerUserAccount(registerAccount);
  }

  @Transactional
  public UserAccountResponse updateAccount(UserAccountDetails userDetails,
      AccountUpdateRequest request)
      throws NotUniqueException, InvalidPasswordChangeException {

    String publicId = userDetails.getAccount().getPublicId();

    // 更新情報がない場合
    if (ObjectUtils.allNull(request.getUserName(), request.getEmail(),
        request.getCurrentPassword(), request.getNewPassword())) {
      return null;
    }

    // パスワード更新事前処理
    String hashedPassword = null;
    boolean currentIsNull = request.getCurrentPassword() == null;
    boolean newIsNull = request.getNewPassword() == null;

    // 現行パスワードと新パスワードがどちらかしか入力されていない場合(排他的論理和同等です)
    if (currentIsNull != newIsNull) {
      throw new InvalidPasswordChangeException(
          "パスワード変更には現在のパスワードと新しいパスワードの両方が必要です");
    } else if (!currentIsNull && !newIsNull) {
      if (!passwordEncoder.matches(request.getCurrentPassword(), userDetails.getPassword())) {
        throw new InvalidPasswordChangeException("現在のパスワードをご確認ください");
      }
      hashedPassword = passwordEncoder.encode(request.getNewPassword());
    }

    if (request.getEmail() != null && repository.existsByEmailExcludingUser(publicId,
        request.getEmail())) {
      throw new NotUniqueException("email", "このメールアドレスは使用できません");
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

  private void refreshSecurityContext(UserAccountDetails userDetails, UserAccount updateAccount) {
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

  @Transactional
  public void deleteAccount(String publicId) {
    if (repository.findAccountByPublicId(publicId) == null) {
      throw new RecordNotFoundException("account not found");
    }
    repository.deleteAccount(publicId);
  }

}
