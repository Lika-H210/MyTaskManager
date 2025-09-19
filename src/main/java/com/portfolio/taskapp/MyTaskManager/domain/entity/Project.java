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

  @Schema(description = "プロジェクトID")
  @JsonIgnore
  private Integer id;

  @Schema(description = "このプロジェクトの責任ユーザーのId")
  @JsonIgnore
  private Integer userAccountId;

  @Schema(description = "UUID形式の公開ID")
  private String publicId;

  @Schema(description = "プロジェクト名")
  private String projectCaption;

  @Schema(description = "プロジェクトの詳細説明")
  private String description;

  @Schema(description = "ステータス（ACTIVE/ARCHIVED）")
  private ProjectStatus status;

  @Schema(description = "作成日時")
  private LocalDateTime createdAt;

  @Schema(description = "更新日時")
  private LocalDateTime updatedAt;

  @Schema(description = "論理削除用の削除フラグ(削除=true)")
  private boolean isDeleted;

}
