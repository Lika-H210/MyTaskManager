package com.portfolio.taskapp.MyTaskManager.user.model.update;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "アカウントのEmail情報更新用DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountEmailUpdateRequest {

  @Schema(description = "メールアドレス（ユニーク）")
  @NotEmpty(message = "更新するメルアドレスを入力してください。")
  @Email(message = "正しいメールアドレス形式で入力してください")
  @Size(max = 100, message = "メールアドレスは100文字以下で入力してください")
  private String email;

}
