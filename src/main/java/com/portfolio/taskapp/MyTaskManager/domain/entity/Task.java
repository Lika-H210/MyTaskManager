package com.portfolio.taskapp.MyTaskManager.domain.entity;

import com.portfolio.taskapp.MyTaskManager.domain.enums.TaskPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "タスクを定義するエンティティクラス")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Task {

  // INT AUTO_INCREMENT PRIMARY KEY
  @Schema(description = "タスクID（自動採番）")
  private Integer id;

  // FOREIGN KEY (project_id) REFERENCES projects(id)
  @Schema(description = "所属プロジェクト")
  private Integer projectId;

  // CHAR(36) NOT NULL UNIQUE
  @Schema(description = "UUID形式の公開ID")
  private String publicId;

  // parent_task_id INT REFERENCES tasks(id)
  @Schema(description = "親タスクのId(自身が親の場合はNull)")
  private Integer parentTaskId;

  // VARCHAR(100) NOT NULL
  @Schema(description = "タスクの名前")
  private String taskCaption;

  // TEXT
  @Schema(description = "タスクの詳細説明")
  private String description;

  // NOT NULL
  @Schema(description = "期限日")
  private LocalDate dueDate;

  @Schema(description = "見積もり時間（単位:min）")
  private int estimatedTime;

  // DEFAULT 0
  @Schema(description = "実績時間（単位:min）")
  private int actualTime;

  // DEFAULT 0
  @Schema(description = "進捗率（%）")
  private int progress;

  // ENUM('HIGH','MEDIUM','LOW') DEFAULT 'LOW'
  @Schema(description = "優先度")
  private TaskPriority priority;

  @Schema(description = "作成日時")
  private LocalDateTime createdAt;

  @Schema(description = "更新日時")
  private LocalDateTime updatedAt;

}
