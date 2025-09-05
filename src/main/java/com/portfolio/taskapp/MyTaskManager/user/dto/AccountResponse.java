package com.portfolio.taskapp.MyTaskManager.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * アカウント情報を返却するレスポンス DTO。
 * <p>
 * API 呼び出し時に利用者へ返されるアカウントの基本情報を保持し、パスワード及び内部Idは含みません。
 */
@Schema(description = "アカウントに関するレスポンス内容")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {

  /**
   * アカウントの公開 ID。 内部 ID ではなく外部公開用のUUID形式のID。
   */
  @Schema(description = "UUID形式の公開ID")
  private String publicId;

  /**
   * ユーザー表示名。
   */
  @Schema(description = "ユーザー表示名")
  private String userName;

  /**
   * メールアドレス
   */
  @Schema(description = "メールアドレス（ユニーク）")
  private String email;

  /**
   * アカウント作成日時。
   */
  @Schema(description = "作成日時")
  private LocalDateTime createdAt;

  /**
   * アカウント更新日時。
   */
  @Schema(description = "更新日時")
  private LocalDateTime updatedAt;

}
