package com.portfolio.taskapp.MyTaskManager.task.service.converter;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Task;
import com.portfolio.taskapp.MyTaskManager.domain.model.TaskTree;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TaskConverter {

  public List<TaskTree> convertToTaskTreeList(List<Task> taskList) {
    //taskListが空の場合
    if (taskList == null || taskList.isEmpty()) {
      return Collections.emptyList();
    }

    // 子タスクのみのMap(Keyは親タスクのId)
    Map<Integer, List<Task>> childTaskMap = taskList.stream()
        .filter(task -> task.getParentTaskId() != null)
        .collect(Collectors.groupingBy(Task::getParentTaskId));

    // 親タスク+子タスクリスト（TaskTree)に変換しリスト化
    return taskList.stream()
        .filter(task -> task.getParentTaskId() == null)
        .map(parentTask -> new TaskTree(
            parentTask,
            childTaskMap.getOrDefault(parentTask.getId(), Collections.emptyList())
        ))
        .toList();
  }

}
