package com.portfolio.taskapp.MyTaskManager.user;

import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private UserRepository repository;
  private PasswordEncoder passwordEncoder;

  @Autowired
  public UserService(UserRepository repository,
      PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
  }


  @Transactional
  public void registerUserAccount(UserAccount account) {
    String hashedPassword = passwordEncoder.encode(account.getPassword());
    account.setPassword(hashedPassword);
    account.setPublicId(UUID.randomUUID().toString());

    // Todo:email重複時の検査例外をthrow

    repository.registerUserAccount(account);
  }

}
