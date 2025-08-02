package com.portfolio.taskapp.MyTaskManager.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserAccountRequest {

  @Schema(description = "ユーザー名")
  @NotBlank(message = "ユーザー名は必須です")
  @Size(max = 50)
  private String userName;

  @Schema(description = "メールアドレス（ユニーク）")
  @NotBlank(message = "メールアドレスは必須です")
  @Size(max = 255)
  private String email;

  @Schema(description = "パスワード")
  @NotBlank(message = "パスワードは必須です")
  @Size(min = 8, max = 50, message = "パスワードは8文字以上50文字以下で入力してください")
  @Pattern(
      regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=]+$",
      message = "パスワードは英数字と記号(!@#$%^&*()_+-=)のみ使用可能です"
  )
  private String password;

}
