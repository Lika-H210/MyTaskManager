package com.portfolio.taskapp.MyTaskManager.user.service;

import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import com.portfolio.taskapp.MyTaskManager.user.mapper.UserAccountMapper;
import com.portfolio.taskapp.MyTaskManager.user.model.UserAccountRequest;
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
  public void registerUser(UserAccountRequest request) {
    String publicId = UUID.randomUUID().toString();
    String hashedPassword = passwordEncoder.encode(request.getPassword());
    UserAccount registerAccount = mapper.toUserAccount(request, publicId, hashedPassword);

    // Todo:email重複時の検査例外をthrow

    repository.registerUserAccount(registerAccount);
  }

}
