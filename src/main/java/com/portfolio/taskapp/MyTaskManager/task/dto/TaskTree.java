package com.portfolio.taskapp.MyTaskManager.task.dto;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 親タスクとその子タスクの階層構造を表す DTO。 親子タスクをツリー構造で返す際に使用します。
 */
@Schema(description = "タスクの親子関係の階層構造を定義するDTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskTree {

  /**
   * 親タスク
   */
  @Schema(description = "親タスク")
  private Task parentTask;

  /**
   * 親タスクに紐づく子タスクのリスト
   */
  @Schema(description = "親タスクに紐づく子タスクのリスト")
  private List<Task> subtaskList;

}
