package com.portfolio.taskapp.MyTaskManager.user.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountResponse {

  private String publicId;
  private String userName;
  private String email;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

}
