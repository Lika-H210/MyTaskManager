package com.portfolio.taskapp.MyTaskManager.user.mapper;

import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import com.portfolio.taskapp.MyTaskManager.user.model.AccountRegisterRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.AccountResponse;
import com.portfolio.taskapp.MyTaskManager.user.model.AccountUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.update.AccountEmailUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.update.AccountUserInfoUpdateRequest;
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

  public UserAccount updateRequestToUserAccount(AccountUpdateRequest request, String publicId,
      String hashedPassword) {
    return UserAccount.builder()
        .publicId(publicId)
        .userName(request.getUserName())
        .email(request.getEmail())
        .password(hashedPassword)
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
