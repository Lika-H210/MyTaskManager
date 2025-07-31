package com.portfolio.taskapp.MyTaskManager.auth.service;

import com.portfolio.taskapp.MyTaskManager.auth.model.UserAccountDetails;
import com.portfolio.taskapp.MyTaskManager.auth.repository.UserAccountRepository;
import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserAccountDetailsService implements UserDetailsService {

  private final UserAccountRepository repository;

  @Autowired
  public UserAccountDetailsService(UserAccountRepository repository) {
    this.repository = repository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserAccount account = repository.findAccountByEmail(email);
    if (account == null) {
      log.warn("Login failed. Invalid credentials provided");
      throw new UsernameNotFoundException("認証に失敗しました");
    }
    return new UserAccountDetails(account);
  }
}
