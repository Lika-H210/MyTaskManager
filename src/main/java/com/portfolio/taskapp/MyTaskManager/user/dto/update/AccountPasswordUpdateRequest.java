package com.portfolio.taskapp.MyTaskManager.user.dto.update;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "アカウントのPassword更新用DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountPasswordUpdateRequest {

  @Schema(description = "現在のパスワード")
  @NotEmpty(message = "現在のパスワードを入力してください")
  @Size(max = 50, message = "パスワードは50文字以下で入力してください")
  private String currentPassword;

  @Schema(description = "新しいパスワード")
  @NotEmpty(message = "新しいパスワードを入力してください")
  @Size(min = 8, max = 50, message = "パスワードは8文字以上50文字以下で入力してください")
  @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=]+$",
      message = "パスワードは英数字と記号(!@#$%^&*()_+-=)のみ使用可能です")
  private String newPassword;

}
