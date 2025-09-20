package com.portfolio.taskapp.MyTaskManager.auth.service;

import com.portfolio.taskapp.MyTaskManager.auth.details.UserAccountDetails;
import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import com.portfolio.taskapp.MyTaskManager.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserAccountDetailsService implements UserDetailsService {

  private final UserRepository repository;

  @Autowired
  public UserAccountDetailsService(UserRepository repository) {
    this.repository = repository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    if (email == null || email.isBlank()) {
      log.warn("Login failed. reason=missing_email");
      throw new UsernameNotFoundException("Login failed.");
    }

    UserAccount account = repository.findAccountByEmail(email);
    if (account == null) {
      log.warn("Login failed. reason=invalid_credentials");
      throw new UsernameNotFoundException("Login failed.");
    }
    return new UserAccountDetails(account);
  }
}
