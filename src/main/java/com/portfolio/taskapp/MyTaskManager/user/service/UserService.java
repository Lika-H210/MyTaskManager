package com.portfolio.taskapp.MyTaskManager.user.service;

import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import com.portfolio.taskapp.MyTaskManager.user.mapper.UserAccountMapper;
import com.portfolio.taskapp.MyTaskManager.user.model.UserAccountRequest;
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


  @Transactional
  public void registerUserAccount(UserAccountRequest account) {
    String hashedPassword = passwordEncoder.encode(account.getPassword());
    account.setPassword(hashedPassword);
    String publicId = UUID.randomUUID().toString();
    UserAccount registerAccount = mapper.toUserAccount(account, publicId);

    // Todo:email重複時の検査例外をthrow

    repository.registerUserAccount(registerAccount);
  }

}
