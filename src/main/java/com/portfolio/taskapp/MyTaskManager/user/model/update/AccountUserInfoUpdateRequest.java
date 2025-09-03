package com.portfolio.taskapp.MyTaskManager.user.model.update;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "アカウントのユーザー情報（認証情報以外）の更新用DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountUserInfoUpdateRequest {

  @Schema(description = "ユーザー名")
  @NotBlank(message = "ユーザー名を入力してください")
  @Size(max = 50, message = "ユーザー名は50字以内で入力してください")
  @Pattern(regexp = "^[a-zA-Z0-9\\u3040-\\u309F\\u30A0-\\u30FF\\u4E00-\\u9FAF _\\-\\u3000]+$",
      message = "ユーザー名には英数字・ひらがな・カタカナ・漢字、スペース、記号（_ -）のみ使用できます")

  private String userName;

}
