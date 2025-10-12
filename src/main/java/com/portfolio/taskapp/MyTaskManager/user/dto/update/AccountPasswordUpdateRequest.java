package com.portfolio.taskapp.MyTaskManager.user.dto.update;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * アカウントのパスワード更新用リクエスト DTO。
 */
@Schema(description = "アカウントのPassword更新用リクエストDTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountPasswordUpdateRequest {

  /**
   * 現在のパスワード。
   * <p>
   * パスワード更新時の照合用フィールドです。 登録時に字数下限及びパターンの制約がありますが、入力チェックはせず、パスワード照合時にエラーにします。
   */
  @Schema(description = "現在のパスワード", example = "currentPassword")
  @NotEmpty(message = "現在のパスワードは必須です")
  @Size(max = 50, message = "パスワードは50文字以下で入力してください")
  private String currentPassword;

  /**
   * 新しいパスワード。 必須入力。8〜50文字の範囲で、半角英数字および一部記号のみ利用可能。
   */
  @Schema(description = "新しいパスワード", example = "newPassword")
  @NotEmpty(message = "新しいパスワードは必須です")
  @Size(min = 8, max = 50, message = "パスワードは8文字以上50文字以下で入力してください")
  @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=]+$",
      message = "パスワードに使えない文字が含まれています。")
  private String newPassword;

}
