package com.portfolio.taskapp.MyTaskManager.user.service;

import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import com.portfolio.taskapp.MyTaskManager.exception.NotUniqueException;
import com.portfolio.taskapp.MyTaskManager.exception.RecordNotFoundException;
import com.portfolio.taskapp.MyTaskManager.user.mapper.UserAccountMapper;
import com.portfolio.taskapp.MyTaskManager.user.model.ProfileUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.UserAccountCreateRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.UserAccountResponse;
import com.portfolio.taskapp.MyTaskManager.user.repository.UserRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
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
  public UserAccountResponse updateProfile(String publicId, ProfileUpdateRequest request)
      throws NotUniqueException {

    if (repository.existsByEmailExcludingUser(publicId, request.getEmail())) {
      throw new NotUniqueException("email", "このメールアドレスは使用できません");
    }

    UserAccount account = mapper.profileToUserAccount(request, publicId);

    repository.updateProfile(account);
    UserAccount updatedAccount = repository.findAccountByPublicId(publicId);

    return mapper.toUserAccountResponse(updatedAccount);
  }

  @Transactional
  public void deleteAccount(String publicId) {
    if (repository.findAccountByPublicId(publicId) == null) {
      throw new RecordNotFoundException("account not found");
    }
    repository.deleteAccount(publicId);
  }

}
