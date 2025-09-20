package com.portfolio.taskapp.MyTaskManager.auth.details;

import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserAccountDetails implements UserDetails {

  private final UserAccount userAccount;

  public UserAccountDetails(UserAccount userAccount) {
    this.userAccount = userAccount;
  }

  // 一律のアクセス制御のため権限は設定していません
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

  @Override
  public String getPassword() {
    return userAccount.getPassword();
  }

  @Override
  public String getUsername() {
    return userAccount.getEmail();
  }

  public UserAccount getAccount() {
    return userAccount;
  }

}
