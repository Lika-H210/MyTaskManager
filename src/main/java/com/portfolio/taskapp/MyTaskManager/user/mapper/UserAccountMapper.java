package com.portfolio.taskapp.MyTaskManager.user.mapper;

import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import com.portfolio.taskapp.MyTaskManager.user.model.UserAccountRequest;
import org.springframework.stereotype.Component;

@Component
public class UserAccountMapper {

  public UserAccount toUserAccount(UserAccountRequest request, String publicId,
      String hashedPassword) {
    return UserAccount.builder()
        .publicId(publicId)
        .userName(request.getUserName())
        .email(request.getEmail())
        .password(hashedPassword)
        .build();
  }

}
