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

/**
 * タスクの登録・更新用リクエスト DTO。 タスクを登録・更新する API のリクエストボディとして使用します。
 */
@Schema(description = "タスクの登録・更新用リクエストDTO")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {

  /**
   * タスク名。必須、最大100文字
   */
  @Schema(description = "タスク名", example = "テスト計画")
  @NotBlank(message = "タスク名は必須です。")
  @Size(max = 100, message = "タスク名は100文字以下で入力してください")
  private String taskCaption;

  /**
   * タスクの詳細説明。最大1000文字
   */
  @Schema(description = "タスクの詳細説明", example = "テスト計画作成")
  @NotNull(message = "未入力は空にしてください。")
  @Size(max = 1000, message = "タスクの詳細説明は1000文字以下で入力してください")
  private String description;

  /**
   * タスクの期限日。必須
   */
  @Schema(description = "期限日", example = "2025-01-01")
  @NotNull(message = "期限日は必須です")
  private LocalDate dueDate;

  /**
   * 見積もり時間（分単位）。必須、正の整数
   */
  @Schema(description = "見積もり時間（単位:min）", example = "120")
  @NotNull(message = "見積時間は必須です")
  @Positive(message = "入力値は分単位で正の整数値を入力してください")
  private int estimatedTime;

  /**
   * 実績時間（分単位）。
   */
  @Schema(description = "実績時間（単位:min）※新規登録時は0を設定してください", example = "60")
  @NotNull(message = "実績時間は必須です")
  @PositiveOrZero(message = "入力値は分単位で0以上の整数値を入力してください")
  private int actualTime;

  /**
   * 進捗率（%）。0〜100
   */
  @Schema(description = "進捗率（%）※新規登録時は0を設定してください", example = "50")
  @NotNull(message = "進捗率は必須です")
  @Min(value = 0, message = "入力値は0以上の整数値を入力してください")
  @Max(value = 100, message = "入力値は100以下の整数値を入力してください")
  private int progress;

  /**
   * タスクの優先度。
   */
  @Schema(description = "優先度　※未設定時はLOWを返してください", example = "LOW")
  @NotNull(message = "優先度は必須です")
  private TaskPriority priority;

}
