package com.portfolio.taskapp.MyTaskManager.service;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Project;
import com.portfolio.taskapp.MyTaskManager.domain.entity.Task;
import com.portfolio.taskapp.MyTaskManager.domain.model.TaskTree;
import com.portfolio.taskapp.MyTaskManager.repository.TaskRepository;
import com.portfolio.taskapp.MyTaskManager.service.converter.TaskConverter;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

  private TaskRepository repository;
  private TaskConverter converter;

  @Autowired
  public TaskService(TaskRepository repository, TaskConverter converter) {
    this.repository = repository;
    this.converter = converter;
  }

  public List<Project> getUserProjects(String userPublicId) {
    Integer userId = repository.findUserIdByUserPublicId(userPublicId);
    if (userId == null) {
      //Todo:例外処理が返るように要修正（例外実装後）
      return Collections.emptyList();
    }
    return repository.findProjectsByUserId(userId);
  }

  public List<TaskTree> getTasksByProjectPublicId(String projectPublicId) {
    Integer projectId = repository.findProjectIdByProjectPublicId(projectPublicId);
    List<Task> taskList = repository.findTasksByProjectId(projectId);
    return converter.convertToTaskTreeList(taskList);
  }

  public TaskTree getTaskTreeByTaskPublicId(String taskPublicId) {
    Integer taskId = repository.findTaskIdByTaskPublicId(taskPublicId);
    List<Task> taskList = repository.findTasksByTaskId(taskId);
    List<TaskTree> taskTreeList = converter.convertToTaskTreeList(taskList);

    if (taskTreeList.size() != 1) {
      // TODO: カスタム例外（TaskTreeNotFoundException）に差し替える
      throw new IllegalStateException("指定されたタスクに対応するTaskTreeが1件ではありません");
    }
    return taskTreeList.getFirst();
  }

}
