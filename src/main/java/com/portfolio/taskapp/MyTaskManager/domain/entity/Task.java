package com.portfolio.taskapp.MyTaskManager.domain.entity;

import com.portfolio.taskapp.MyTaskManager.domain.enums.TaskPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;

@Schema(description = "タスクを定義するエンティティクラス")
@Builder
public class Task {

  // INT AUTO_INCREMENT PRIMARY KEY
  @Schema(description = "タスクID（自動採番）")
  private Integer id;

  // FOREIGN KEY (project_id) REFERENCES projects(id)
  @Schema(description = "所属プロジェクト")
  private Project projectId;

  // CHAR(36) NOT NULL
  @Schema(description = "UUID形式の公開ID")
  private String publicId;

  // parent_task_id INT REFERENCES tasks(id)
  @Schema(description = "親タスク（サブタスクの場合）")
  private Task parentTask;

  // VARCHAR(100) NOT NULL
  @Schema(description = "タスクの名前")
  private String taskCaption;

  // TEXT
  @Schema(description = "タスクの詳細説明")
  private String description;

  // NOT NULL
  @Schema(description = "期限日")
  private LocalDate dueDate;

  // NOT NULL
  @Schema(description = "見積もり時間（単位:hour）")
  private Double estimatedTime;

  // DEFAULT NULL
  @Schema(description = "実績時間（単位:min）")
  private Integer actualTimeMinutes;

  // NOT NULL DEFAULT 0
  @Schema(description = "進捗（%）")
  private Integer progress;

  // ENUM('HIGH','MEDIUM','LOW') DEFAULT 'LOW'
  @Schema(description = "優先度")
  private TaskPriority priority;

  @Schema(description = "作成日時")
  private LocalDateTime createdAt;

  @Schema(description = "更新日時")
  private LocalDateTime updatedAt;

}
