package com.portfolio.taskapp.MyTaskManager.task.dto;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskTree {

  @Schema(description = "親タスク")
  private Task parentTask;

  @Schema(description = "親タスクに紐づく子タスクのリスト")
  private List<Task> subtaskList;

}
