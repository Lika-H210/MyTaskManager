package com.portfolio.taskapp.MyTaskManager.service;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Project;
import com.portfolio.taskapp.MyTaskManager.repository.TaskRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

  private TaskRepository repository;

  @Autowired
  public TaskService(TaskRepository repository) {
    this.repository = repository;
  }

  public List<Project> getMyProject(String userPublicId) {
    Integer userId = repository.getUserId(userPublicId);
    return repository.getProjectList(userId);
  }

}
