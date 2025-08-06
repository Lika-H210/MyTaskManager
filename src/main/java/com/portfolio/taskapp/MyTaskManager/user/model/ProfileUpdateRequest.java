package com.portfolio.taskapp.MyTaskManager.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileUpdateRequest {

  @Schema(description = "ユーザー名")
  @NotNull(message = "ユーザー名は必須です")
  @Size(max = 50, message = "ユーザー名は50字以内で入力してください")
  @Pattern(
      regexp = "^[a-zA-Z0-9\\u3040-\\u309F\\u30A0-\\u30FF\\u4E00-\\u9FAF _\\-\\u3000]+$",
      message = "ユーザー名に使用できない文字が含まれています"
  )
  private String userName;

  @Schema(description = "メールアドレス（ユニーク）")
  @NotEmpty(message = "メールアドレスは必須です")
  @Email(message = "正しいメールアドレス形式で入力してください")
  @Size(max = 100, message = "メールアドレスは100文字以下で入力してください")
  private String email;

}
