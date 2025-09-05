package com.portfolio.taskapp.MyTaskManager.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.portfolio.taskapp.MyTaskManager.domain.enums.TaskPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * タスクを定義するエンティティクラス。
 * <p>
 * DB の tasks テーブルに対応します。スポンス時の使用を考慮し内部Idのfieldは@JsonIgnore指定しています。
 */
@Schema(description = "タスクを定義するエンティティクラス")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Task {

  @Schema(description = "タスクID")
  @JsonIgnore
  private Integer id;

  @Schema(description = "所属プロジェクト")
  @JsonIgnore
  private Integer projectId;

  @Schema(description = "UUID形式の公開ID")
  private String publicId;

  @Schema(description = "親タスクのId(自身が親の場合はNull)")
  @JsonIgnore
  private Integer parentTaskId;

  @Schema(description = "タスクの名前")
  private String taskCaption;

  @Schema(description = "タスクの詳細説明")
  private String description;

  @Schema(description = "期限日")
  private LocalDate dueDate;

  @Schema(description = "見積もり時間（単位:min）")
  private int estimatedTime;

  @Schema(description = "実績時間（単位:min）")
  private int actualTime;

  @Schema(description = "進捗率（%）")
  private int progress;

  @Schema(description = "優先度")
  private TaskPriority priority;

  @Schema(description = "作成日時")
  private LocalDateTime createdAt;

  @Schema(description = "更新日時")
  private LocalDateTime updatedAt;

  @Schema(description = "論理削除用の削除フラグ(削除=true)")
  private boolean isDeleted;

}
