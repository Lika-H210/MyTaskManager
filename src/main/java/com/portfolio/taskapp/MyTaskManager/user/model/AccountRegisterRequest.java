package com.portfolio.taskapp.MyTaskManager.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "アカウント登録のリクエスト内容")
@Getter
@AllArgsConstructor
public class AccountRegisterRequest {

  @Schema(description = "ユーザー名")
  @NotBlank(message = "ユーザー名は必須です")
  @Size(max = 50, message = "ユーザー名は50字以内で入力してください")
  @Pattern(
      regexp = "^[a-zA-Z0-9\\u3040-\\u309F\\u30A0-\\u30FF\\u4E00-\\u9FAF _\\-\\u3000]+$",
      message = "ユーザー名には英数字・ひらがな・カタカナ・漢字、スペース、記号（_ -）のみ使用できます"
  )
  private String userName;

  @Schema(description = "メールアドレス（ユニーク）")
  @NotEmpty(message = "メールアドレスは必須です")
  @Email(message = "正しいメールアドレス形式で入力してください")
  @Size(max = 100, message = "メールアドレスは100文字以下で入力してください")
  private String email;

  @Schema(description = "パスワード")
  @NotNull(message = "パスワードは必須です")
  @Size(min = 8, max = 50, message = "パスワードは8文字以上50文字以下で入力してください")
  @Pattern(
      regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=]+$",
      message = "パスワードは英数字と記号(!@#$%^&*()_+-=)のみ使用可能です"
  )
  private String password;

}
