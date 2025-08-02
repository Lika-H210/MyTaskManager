package com.portfolio.taskapp.MyTaskManager.user.mapper;

import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import com.portfolio.taskapp.MyTaskManager.user.model.UserAccountRequest;
import org.springframework.stereotype.Component;

@Component
public class UserAccountMapper {

  public UserAccount toUserAccount(UserAccountRequest account, String publicId) {
    return UserAccount.builder()
        .publicId(publicId)
        .userName(account.getUserName())
        .email(account.getEmail())
        .password(account.getPassword())
        .build();
  }

}
