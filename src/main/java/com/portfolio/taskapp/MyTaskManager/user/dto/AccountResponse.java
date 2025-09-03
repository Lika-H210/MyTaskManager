package com.portfolio.taskapp.MyTaskManager.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "アカウントに関するレスポンス内容")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {

  @Schema(description = "UUID形式の公開ID")
  private String publicId;

  @Schema(description = "ユーザー表示名")
  private String userName;

  @Schema(description = "メールアドレス（ユニーク）")
  private String email;

  @Schema(description = "作成日時")
  private LocalDateTime createdAt;

  @Schema(description = "更新日時")
  private LocalDateTime updatedAt;

}
