package com.portfolio.taskapp.MyTaskManager.user.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserAccountResponse {

  private String publicId;
  private String userName;
  private String email;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

}
