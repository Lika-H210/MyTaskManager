package com.portfolio.taskapp.MyTaskManager.task.service;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Project;
import com.portfolio.taskapp.MyTaskManager.domain.entity.Task;
import com.portfolio.taskapp.MyTaskManager.exception.custom.RecordNotFoundException;
import com.portfolio.taskapp.MyTaskManager.task.mapper.ProjectTaskMapper;
import com.portfolio.taskapp.MyTaskManager.task.model.ProjectRequest;
import com.portfolio.taskapp.MyTaskManager.task.model.TaskRequest;
import com.portfolio.taskapp.MyTaskManager.task.model.TaskTree;
import com.portfolio.taskapp.MyTaskManager.task.repository.TaskRepository;
import com.portfolio.taskapp.MyTaskManager.task.service.converter.TaskConverter;
import java.util.List;
import java.util.Optional;
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
    Integer userId = requireUserIdByPublicId(userPublicId);
    return repository.findProjectsByUserId(userId);
  }

  public List<TaskTree> getTasksByProjectPublicId(String projectPublicId) {
    Integer projectId = requireProjectIdByPublicId(projectPublicId);

    List<Task> taskList = repository.findTasksByProjectId(projectId);
    return converter.convertToTaskTreeList(taskList);
  }

  public TaskTree getTaskTreeByTaskPublicId(String taskPublicId) {
    Integer taskId = Optional.ofNullable(repository.findTaskIdByTaskPublicId(taskPublicId))
        .orElseThrow(() -> new RecordNotFoundException("task not found"));

    List<Task> taskList = repository.findTasksByTaskId(taskId);
    List<TaskTree> taskTreeList = converter.convertToTaskTreeList(taskList);

    if (taskTreeList.size() != 1) {
      throw new IllegalStateException(
          "TaskTree count mismatch: expected 1, but got " + taskTreeList.size());
    }
    return taskTreeList.getFirst();
  }

  @Transactional
  public Project createProject(ProjectRequest request, String userPublicId) {
    Integer userId = requireUserIdByPublicId(userPublicId);

    String publicId = UUID.randomUUID().toString();
    Project project = mapper.toProject(request, userId, publicId);

    repository.createProject(project);

    return project;
  }

  @Transactional
  public Task createParentTask(TaskRequest request, String projectPublicId) {
    Integer projectId = requireProjectIdByPublicId(projectPublicId);

    String publicId = UUID.randomUUID().toString();
    Task task = mapper.toTask(request, projectId, publicId);

    repository.createTask(task);

    return task;
  }

  @Transactional
  public Task createSubtask(TaskRequest request, String taskPublicId) {
    Task parentTask = Optional.ofNullable(repository.findTaskByTaskPublicId(taskPublicId))
        .orElseThrow(() -> new RecordNotFoundException("parent task not found"));

    String publicId = UUID.randomUUID().toString();
    Task task = mapper.toSubtask(request, parentTask, publicId);

    repository.createTask(task);

    return task;
  }

  @Transactional
  public Project updateProject(ProjectRequest request, String projectPublicId) {

    Project project = mapper.toProject(request, null, projectPublicId);
    int updateRows = repository.updateProject(project);

    if (updateRows == 0) {
      throw new RecordNotFoundException("project not found");
    }

    return repository.findProjectByProjectPublicId(projectPublicId);
  }

  @Transactional
  public Task updateTask(TaskRequest request, String taskPublicId) {

    Task task = mapper.toTask(request, null, taskPublicId);
    int updateRows = repository.updateTask(task);

    if (updateRows == 0) {
      throw new RecordNotFoundException("task not found");
    }

    return repository.findTaskByTaskPublicId(taskPublicId);
  }

  @Transactional
  public void deleteProject(String projectPublicId) {
    // 削除対象のnullチェック
    if (repository.findProjectIdByProjectPublicId(projectPublicId) == null) {
      throw new RecordNotFoundException("project not found");
    }
    repository.deleteProject(projectPublicId);
  }

  @Transactional
  public void deleteTask(String taskPublicId) {
    // 削除対象のnullチェック
    if (repository.findTaskIdByTaskPublicId(taskPublicId) == null) {
      throw new RecordNotFoundException("task not found");
    }
    repository.deleteTask(taskPublicId);
  }

  private Integer requireUserIdByPublicId(String userPublicId) {
    return Optional.ofNullable(repository.findUserIdByUserPublicId(userPublicId))
        .orElseThrow(() -> new RecordNotFoundException("user not found"));
  }

  private Integer requireProjectIdByPublicId(String projectPublicId) {
    return Optional.ofNullable(repository.findProjectIdByProjectPublicId(projectPublicId))
        .orElseThrow(() -> new RecordNotFoundException("project not found"));
  }

}
