package com.portfolio.taskapp.MyTaskManager.task.dto;

import com.portfolio.taskapp.MyTaskManager.domain.enums.TaskPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "タスクの登録・更新用リクエスト項目")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {

  @Schema(description = "タスク名")
  @NotBlank(message = "タスク名は必須です。")
  @Size(max = 100, message = "タスク名は100字以内で入力してください")
  private String taskCaption;

  @Schema(description = "タスクの詳細説明")
  @NotNull(message = "タスクの詳細説明はNullを許容しません。未入力は空文字にしてください。")
  @Size(max = 1000, message = "タスクの詳細説明は1000字以内で入力してください")
  private String description;

  @Schema(description = "期限日")
  @NotNull(message = "期限日を設定してください")
  private LocalDate dueDate;

  @Schema(description = "見積もり時間（単位:min）")
  @Positive(message = "入力値は分単位で正の整数値を入力してください")
  private int estimatedTime;

  @Schema(description = "実績時間（単位:min）※新規登録時は0を設定してください")
  @PositiveOrZero(message = "入力値は分単位で正の整数値を入力してください")
  private int actualTime;

  @Schema(description = "進捗率（%）※新規登録時は0を設定してください")
  @Min(value = 0, message = "入力値は0以上の整数値を入力してください")
  @Max(value = 100, message = "入力値は100以下の整数値を入力してください")
  private int progress;

  @Schema(description = "優先度　※未設定時はLOWを返してください")
  @NotNull(message = "優先度は必須です")
  private TaskPriority priority;

}
