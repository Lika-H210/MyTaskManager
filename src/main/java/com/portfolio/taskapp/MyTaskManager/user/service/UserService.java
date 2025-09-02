package com.portfolio.taskapp.MyTaskManager.user.service;

import com.portfolio.taskapp.MyTaskManager.auth.model.UserAccountDetails;
import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import com.portfolio.taskapp.MyTaskManager.exception.custom.InvalidPasswordChangeException;
import com.portfolio.taskapp.MyTaskManager.exception.custom.NotUniqueException;
import com.portfolio.taskapp.MyTaskManager.exception.custom.RecordNotFoundException;
import com.portfolio.taskapp.MyTaskManager.user.mapper.UserAccountMapper;
import com.portfolio.taskapp.MyTaskManager.user.model.AccountRegisterRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.AccountResponse;
import com.portfolio.taskapp.MyTaskManager.user.model.update.AccountEmailUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.update.AccountPasswordUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.update.AccountUserInfoUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.repository.UserRepository;
import java.util.Optional;
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
  public AccountResponse updatePassword(UserAccountDetails userDetails,
      AccountPasswordUpdateRequest request) throws InvalidPasswordChangeException {

    if (!passwordEncoder.matches(request.getCurrentPassword(), userDetails.getPassword())) {
      throw new InvalidPasswordChangeException("現在のパスワードをご確認ください");
    }

    String newHashedPassword = passwordEncoder.encode(request.getNewPassword());

    UserAccount account = mapper
        .updatePasswordRequestToUserAccount(newHashedPassword,
            userDetails.getAccount().getPublicId());
    repository.updateAccount(account);

    refreshSecurityContext(userDetails, account);

    return mapper.toUserAccountResponse(account);
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
