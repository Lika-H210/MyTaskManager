package com.portfolio.taskapp.MyTaskManager.domain.entity;

import com.portfolio.taskapp.MyTaskManager.domain.enums.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Schema(description = "プロジェクトを定義するエンティティクラス")
@Builder
public class Project {

  // AUTO_INCREMENT PRIMARY KEY
  @Schema(description = "プロジェクトID（自動採番）")
  private Integer id;

  // FOREIGN KEY (user_id) REFERENCES users(id)
  @Schema(description = "このプロジェクトの責任ユーザーのId")
  private Integer userId;

  // CHAR(36) NOT NULL
  @Schema(description = "UUID形式の公開ID")
  private String publicId;

  // VARCHAR(100) NOT NULL
  @Schema(description = "プロジェクト名")
  private String projectCaption;

  // TEXT
  @Schema(description = "プロジェクトの詳細説明")
  private String description;

  // ENUM('ACTIVE', 'ARCHIVED') DEFAULT 'ACTIVE'
  @Schema(description = "ステータス（ACTIVE/ARCHIVED）")
  private ProjectStatus status;

  @Schema(description = "作成日時")
  private LocalDateTime createdAt;

  @Schema(description = "更新日時")
  private LocalDateTime updatedAt;

}
