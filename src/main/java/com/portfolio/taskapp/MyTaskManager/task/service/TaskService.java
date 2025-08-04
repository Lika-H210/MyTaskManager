package com.portfolio.taskapp.MyTaskManager.task.service;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Project;
import com.portfolio.taskapp.MyTaskManager.domain.entity.Task;
import com.portfolio.taskapp.MyTaskManager.task.mapper.ProjectTaskMapper;
import com.portfolio.taskapp.MyTaskManager.task.model.ProjectRequest;
import com.portfolio.taskapp.MyTaskManager.task.model.TaskRequest;
import com.portfolio.taskapp.MyTaskManager.task.model.TaskTree;
import com.portfolio.taskapp.MyTaskManager.task.repository.TaskRepository;
import com.portfolio.taskapp.MyTaskManager.task.service.converter.TaskConverter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

  private TaskRepository repository;
  private TaskConverter converter;
  private ProjectTaskMapper mapper;

  @Autowired
  public TaskService(TaskRepository repository, TaskConverter converter, ProjectTaskMapper mapper) {
    this.repository = repository;
    this.converter = converter;
    this.mapper = mapper;
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
      // TODO: カスタム例外（仮：TaskTreeCountMismatchException）に差し替える
      throw new IllegalStateException("指定されたタスクに対応するTaskTreeが1件ではありません");
    }
    return taskTreeList.getFirst();
  }

  @Transactional
  public Project createProject(ProjectRequest request, String userPublicId) {
    Integer userId = repository.findUserIdByUserPublicId(userPublicId);
    String publicId = UUID.randomUUID().toString();
    Project project = mapper.toProject(request, userId, publicId);

    repository.createProject(project);

    return project;
  }

  @Transactional
  public Task createParentTask(TaskRequest request, String projectPublicId) {
    Integer projectId = repository.findProjectIdByProjectPublicId(projectPublicId);
    String publicId = UUID.randomUUID().toString();
    Task task = mapper.toParentTask(request, projectId, publicId);

    repository.createTask(task);

    return task;
  }

  @Transactional
  public Project updateProject(ProjectRequest request, String projectPublicId) {

    Project project = mapper.toProject(request, null, projectPublicId);
    repository.updateProject(project);

    return repository.findProjectByProjectPublicId(projectPublicId);
  }
}
