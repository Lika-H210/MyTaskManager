package com.portfolio.taskapp.MyTaskManager.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * アカウント登録時のリクエスト DTO。
 */
@Schema(description = "アカウント登録用リクエストDTO")
@Getter
@AllArgsConstructor
public class AccountRegisterRequest {

  /**
   * ユーザー名。 必須入力。50文字以下。 利用可能な文字は英数字、ひらがな、カタカナ、漢字、スペース、記号（_ -）。
   */
  @Schema(description = "ユーザー名")
  @NotBlank(message = "ユーザー名は必須です")
  @Size(max = 50, message = "ユーザー名は50文字以下で入力してください")
  @Pattern(
      regexp = "^[a-zA-Z0-9\\u3040-\\u309F\\u30A0-\\u30FF\\u4E00-\\u9FAF _\\-\\u3000]+$",
      message = "ユーザー名には英数字・ひらがな・カタカナ・漢字、スペース、記号（_-）のみ使用できます"
  )
  private String userName;

  /**
   * メールアドレス。 必須入力。メール形式チェック有効。100文字以下。認証に使用されるため、ユニーク制約がかかっています。
   */
  @Schema(description = "メールアドレス（ユニーク）")
  @NotEmpty(message = "メールアドレスは必須です")
  @Email(message = "正しいメールアドレス形式で入力してください")
  @Size(max = 100, message = "メールアドレスは100文字以下で入力してください")
  private String email;

  /**
   * パスワード。 必須入力。8〜50文字の範囲で、半角英数字および一部記号のみ利用可能。
   */
  @Schema(description = "パスワード")
  @NotEmpty(message = "パスワードは必須です")
  @Size(min = 8, max = 50, message = "パスワードは8文字以上50文字以下で入力してください")
  @Pattern(
      regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=]+$",
      message = "パスワードに使えない文字が含まれています。"
  )
  private String password;

}
