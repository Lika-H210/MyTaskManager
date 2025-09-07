package com.portfolio.taskapp.MyTaskManager.user.service.mapper;

import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import com.portfolio.taskapp.MyTaskManager.user.dto.AccountRegisterRequest;
import com.portfolio.taskapp.MyTaskManager.user.dto.AccountResponse;
import com.portfolio.taskapp.MyTaskManager.user.dto.update.AccountEmailUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.dto.update.AccountUserInfoUpdateRequest;
import org.springframework.stereotype.Component;

@Component
public class UserAccountMapper {

  public UserAccount createRequestToUserAccount(AccountRegisterRequest request, String publicId,
      String hashedPassword) {
    return UserAccount.builder()
        .publicId(publicId)
        .userName(request.getUserName())
        .email(request.getEmail())
        .password(hashedPassword)
        .build();
  }

  public UserAccount updateUserInfoRequestToUserAccount(AccountUserInfoUpdateRequest request,
      String publicId) {
    return UserAccount.builder()
        .publicId(publicId)
        .userName(request.getUserName())
        .build();
  }

  public UserAccount updateEmailRequestToUserAccount(AccountEmailUpdateRequest request,
      String publicId) {
    return UserAccount.builder()
        .publicId(publicId)
        .email(request.getEmail())
        .build();
  }

  public UserAccount updatePasswordRequestToUserAccount(String newHashedPassword,
      String publicId) {
    return UserAccount.builder()
        .publicId(publicId)
        .password(newHashedPassword)
        .build();
  }

  public AccountResponse toUserAccountResponse(UserAccount account) {
    return new AccountResponse(
        account.getPublicId(),
        account.getUserName(),
        account.getEmail(),
        account.getCreatedAt(),
        account.getUpdatedAt()
    );
  }

}
