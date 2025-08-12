package com.portfolio.taskapp.MyTaskManager.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateRequest {

  @Schema(description = "ユーザー名")
  @Size(min = 1, max = 50, message = "ユーザー名は1文字以上50字以内で入力してください")
  @Pattern.List({
      @Pattern(regexp = "^[^\\s\u3000].*$", message = "先頭にスペースは使用できません"),
      @Pattern(regexp = "^[a-zA-Z0-9\\u3040-\\u309F\\u30A0-\\u30FF\\u4E00-\\u9FAF _\\-\\u3000]+$",
          message = "ユーザー名には英数字・ひらがな・カタカナ・漢字、スペース、記号（_ -）のみ使用できます")
  })
  private String userName;

  @Schema(description = "メールアドレス（ユニーク）")
  @Email(message = "正しいメールアドレス形式で入力してください")
  @Size(min = 1, max = 100, message = "メールアドレスは1文字以上100文字以下で入力してください")
  private String email;

  @Schema(description = "現在のパスワード：パスワード更新には現在のパスワードと新しいパスワード何れも必要です")
  @Size(min = 8, max = 50, message = "パスワードは8文字以上50文字以下で入力してください")
  @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=]+$",
      message = "パスワードは英数字と記号(!@#$%^&*()_+-=)のみ使用可能です")
  private String currentPassword;

  @Schema(description = "新しいパスワード：パスワード更新には現在のパスワードと新しいパスワード何れも必要です")
  @Size(min = 8, max = 50, message = "パスワードは8文字以上50文字以下で入力してください")
  @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=]+$",
      message = "パスワードは英数字と記号(!@#$%^&*()_+-=)のみ使用可能です")
  private String newPassword;
}
