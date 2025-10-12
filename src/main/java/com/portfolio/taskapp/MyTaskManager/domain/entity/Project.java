package com.portfolio.taskapp.MyTaskManager.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.portfolio.taskapp.MyTaskManager.domain.enums.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * プロジェクトを定義するエンティティクラス。
 * <p>
 * DB の projects テーブルに対応します。 スポンス時の使用を考慮し内部Idのfieldは@JsonIgnore指定しています。
 */
@Schema(description = "プロジェクトを定義するエンティティクラス")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Project {

  @Schema(description = "プロジェクトID", example = "11")
  @JsonIgnore
  private Integer id;

  @Schema(description = "このプロジェクトの責任ユーザーのId", example = "1")
  @JsonIgnore
  private Integer userAccountId;

  @Schema(description = "UUID形式の公開ID", example = "a1b2c3d4-e5f6-7890-abcd-1234567890ef")
  private String publicId;

  @Schema(description = "プロジェクト名", example = "新規プロジェクト")
  private String projectCaption;

  @Schema(description = "プロジェクトの詳細説明", example = "新規プロジェクトの説明文")
  private String description;

  @Schema(description = "ステータス（ACTIVE/ARCHIVED）", example = "ACTIVE")
  private ProjectStatus status;

  @Schema(description = "作成日時", example = "2025-01-01T00:00:00")
  private LocalDateTime createdAt;

  @Schema(description = "更新日時", example = "2025-01-01T00:00:00")
  private LocalDateTime updatedAt;

  @Schema(description = "論理削除用の削除フラグ(削除=true)")
  private boolean isDeleted;

}
