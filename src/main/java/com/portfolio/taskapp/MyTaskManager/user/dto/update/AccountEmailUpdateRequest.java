package com.portfolio.taskapp.MyTaskManager.user.dto.update;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * アカウントのメールアドレス更新用リクエスト DTO。
 */
@Schema(description = "アカウントのEmail更新用リクエストDTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountEmailUpdateRequest {

  /**
   * メールアドレス。 必須入力。形式チェックあり。100文字以下。認証に使用されるため、ユニーク制約がかかっています。
   */
  @Schema(description = "メールアドレス（ユニーク）")
  @NotEmpty(message = "メールアドレスは必須です")
  @Email(message = "正しいメールアドレス形式で入力してください")
  @Size(max = 100, message = "メールアドレスは100文字以下で入力してください")
  private String email;

}
