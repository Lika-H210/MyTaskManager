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
@Schema(description = "アカウントに関するレスポンスDTO")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {

  /**
   * アカウントの公開 ID。 内部 ID ではなく外部公開用のUUID形式のID。
   */
  @Schema(description = "UUID形式の公開ID", example = "a1b2c3d4-e5f6-7890-abcd-1234567890ef")
  private String publicId;

  /**
   * ユーザー表示名。
   */
  @Schema(description = "ユーザー表示名", example = "テスト太郎")
  private String userName;

  /**
   * メールアドレス
   */
  @Schema(description = "メールアドレス", example = "test@ex.com")
  private String email;

  /**
   * アカウント作成日時。
   */
  @Schema(description = "作成日時", example = "2025-01-01T00:00:00")
  private LocalDateTime createdAt;

  /**
   * アカウント更新日時。
   */
  @Schema(description = "更新日時", example = "2025-01-01T00:00:00")
  private LocalDateTime updatedAt;

}
