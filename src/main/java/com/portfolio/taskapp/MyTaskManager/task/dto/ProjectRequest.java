package com.portfolio.taskapp.MyTaskManager.task.dto;

import com.portfolio.taskapp.MyTaskManager.domain.enums.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * プロジェクトの登録・更新用リクエスト DTO。 プロジェクトを登録・更新する API のリクエストボディとして使用します。
 */
@Schema(description = "プロジェクトの登録・更新用リクエストDTO")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequest {

  /**
   * プロジェクト名。必須、最大100文字
   */
  @Schema(description = "プロジェクト名", example = "新規プロジェクト")
  @NotBlank(message = "プロジェクト名は必須です")
  @Size(max = 100, message = "プロジェクト名は100文字以下で入力してください")
  private String projectCaption;

  /**
   * プロジェクトの詳細説明。最大1000文字
   */
  @Schema(description = "プロジェクトの詳細説明", example = "新規プロジェクトの説明文")
  @NotNull(message = "未入力は空にしてください")
  @Size(max = 1000, message = "プロジェクトの詳細説明は1000文字以下で入力してください")
  private String description;

  /**
   * プロジェクトステータス。登録時は ACTIVE 固定
   */
  @Schema(description = "ステータス（ACTIVE/ARCHIVED）※登録時はACTIVE固定としてください", example = "ACTIVE")
  @NotNull(message = "ステータスは必須です")
  private ProjectStatus status;

}
