package com.portfolio.taskapp.MyTaskManager.user.mapper;

import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import com.portfolio.taskapp.MyTaskManager.user.model.AccountRegisterRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.AccountResponse;
import com.portfolio.taskapp.MyTaskManager.user.model.AccountUpdateRequest;
import org.springframework.stereotype.Component;

@Component
public class UserAccountMapper {

  public UserAccount CreateRequestToUserAccount(AccountRegisterRequest request, String publicId,
      String hashedPassword) {
    return UserAccount.builder()
        .publicId(publicId)
        .userName(request.getUserName())
        .email(request.getEmail())
        .password(hashedPassword)
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
