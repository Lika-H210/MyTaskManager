package com.portfolio.taskapp.MyTaskManager.user.service;

import com.portfolio.taskapp.MyTaskManager.auth.model.UserAccountDetails;
import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import com.portfolio.taskapp.MyTaskManager.exception.InvalidPasswordChangeException;
import com.portfolio.taskapp.MyTaskManager.exception.NotUniqueException;
import com.portfolio.taskapp.MyTaskManager.exception.RecordNotFoundException;
import com.portfolio.taskapp.MyTaskManager.user.mapper.UserAccountMapper;
import com.portfolio.taskapp.MyTaskManager.user.model.ProfileUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.UserAccountCreateRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.UserAccountResponse;
import com.portfolio.taskapp.MyTaskManager.user.repository.UserRepository;
import java.util.UUID;
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
  public UserAccountResponse updateProfile(UserAccountDetails userDetails,
      ProfileUpdateRequest request)
      throws NotUniqueException, InvalidPasswordChangeException {

    String publicId = userDetails.getAccount().getPublicId();
    boolean nameIsNull = request.getUserName() == null;
    boolean mailIsNull = request.getEmail() == null;
    boolean currentIsNull = request.getCurrentPassword() == null;
    boolean newIsNull = request.getNewPassword() == null;

    // 更新情報がない場合
    if (nameIsNull && mailIsNull && currentIsNull && newIsNull) {
      return null;
    }

    // 現行パスワードと新パスワードがどちらかしか入力されていない場合
    if (currentIsNull != newIsNull) {
      throw new InvalidPasswordChangeException(
          "パスワードの変更ができません。新・現の両方のパスワード情報が必要です。");
    }

    String updateHashedPassword = null;
    if (!currentIsNull && !newIsNull) {
      if (!passwordEncoder.matches(request.getCurrentPassword(), userDetails.getPassword())) {
        throw new InvalidPasswordChangeException("現在のパスワードが誤っています");
      }
      updateHashedPassword = passwordEncoder.encode(request.getNewPassword());
    }

    if (!mailIsNull && repository.existsByEmailExcludingUser(publicId, request.getEmail())) {
      throw new NotUniqueException("email", "このメールアドレスは使用できません");
    }

    UserAccount updateAccount = mapper.profileToUserAccount(request, publicId,
        updateHashedPassword);
    repository.updateProfile(updateAccount);

    // 認証情報に変更がない場合は処理終了。変更ありは認証情報を更新。
    if (!mailIsNull || updateHashedPassword != null) {
      updateAuthInfo(userDetails, updateAccount);
    }

    return mapper.toUserAccountResponse(updateAccount);
  }

  private static void updateAuthInfo(UserAccountDetails userDetails, UserAccount updateAccount) {
    UserAccount updateUserAccount = UserAccount.builder()
        .publicId(userDetails.getAccount().getPublicId())
        .email(
            updateAccount.getEmail() != null ? updateAccount.getEmail() : userDetails.getUsername())
        .password(updateAccount.getPassword() != null ? updateAccount.getPassword()
            : userDetails.getPassword())
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
