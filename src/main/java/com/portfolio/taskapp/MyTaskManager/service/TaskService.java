package com.portfolio.taskapp.MyTaskManager.service;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Project;
import com.portfolio.taskapp.MyTaskManager.repository.TaskRepository;
import java.util.Collections;
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

  public List<Project> getUserProjects(String userPublicId) {
    Integer userId = repository.findUserIdByUserPublicId(userPublicId);
    if (userId == null) {
      //Todo:例外処理が返るように要修正（例外実装後）
      return Collections.emptyList();
    }
    return repository.findProjectsByUserId(userId);
  }

}
