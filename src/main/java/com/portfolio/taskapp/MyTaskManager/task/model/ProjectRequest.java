package com.portfolio.taskapp.MyTaskManager.task.model;

import com.portfolio.taskapp.MyTaskManager.domain.enums.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "プロジェクトの登録・更新用リクエスト項目")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequest {

  @Schema(description = "プロジェクト名")
  @NotBlank(message = "プロジェクト名は必須です")
  @Size(max = 100, message = "プロジェクト名は文字数を100字以内で入力してください")
  private String projectCaption;


  @Schema(description = "プロジェクトの詳細説明")
  @NotNull(message = "プロジェクトの詳細説明はNullを許容しません。未入力は空文字にしてください。")
  @Size(max = 1000, message = "プロジェクトの詳細説明は文字数を1000字以内で入力してください")
  private String description;

  @Schema(description = "ステータス（ACTIVE/ARCHIVED）※登録時はACTIVE固定としてください。")
  @NotNull(message = "ステータスは必須です")
  private ProjectStatus status;

}
