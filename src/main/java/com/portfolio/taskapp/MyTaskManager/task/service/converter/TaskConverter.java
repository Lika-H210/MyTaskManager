package com.portfolio.taskapp.MyTaskManager.task.service.converter;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Task;
import com.portfolio.taskapp.MyTaskManager.task.dto.TaskTree;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * コントローラー層からフロントエンドへ返却するためのデータ構造になる様に変換を担うコンバータ。
 */
@Component
public class TaskConverter {

  /**
   * タスクリストを、親子関係を示す構造(TaskTree)に変換しリスト化します。
   *
   * @param taskList タスクの一覧（null または空リストでも可）
   * @return 親子タスク(TaskTree)の一覧、パラメータが null または空の場合は空リスト
   */
  public List<TaskTree> convertToTaskTreeList(List<Task> taskList) {
    if (taskList == null || taskList.isEmpty()) {
      return Collections.emptyList();
    }

    // 子タスクのみのMap(Keyは親タスクのId)
    Map<Integer, List<Task>> subtaskMap = taskList.stream()
        .filter(task -> task.getParentTaskId() != null)
        .collect(Collectors.groupingBy(Task::getParentTaskId));

    // 親タスク+子タスクリスト（TaskTree)に変換しリスト化
    return taskList.stream()
        .filter(task -> task.getParentTaskId() == null)
        .map(parentTask -> new TaskTree(
            parentTask,
            subtaskMap.getOrDefault(parentTask.getId(), Collections.emptyList())
        ))
        .toList();
  }

}
