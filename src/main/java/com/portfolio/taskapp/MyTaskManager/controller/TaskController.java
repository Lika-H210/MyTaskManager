package com.portfolio.taskapp.MyTaskManager.controller;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Project;
import com.portfolio.taskapp.MyTaskManager.service.TaskService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {

  private TaskService service;

  @Autowired
  public TaskController(TaskService service) {
    this.service = service;
  }

  @GetMapping("/my-project")
  public List<Project> getProjectList(@RequestParam String userPublicId) {
    // TODO: 本番ではトークンから取得するように変更
    return service.getMyProject(userPublicId);
  }

}
