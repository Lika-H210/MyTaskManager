package com.portfolio.taskapp.MyTaskManager.task.mapper;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Project;
import com.portfolio.taskapp.MyTaskManager.task.model.ProjectRequest;
import org.springframework.stereotype.Component;

@Component
public class ProjectTaskMapper {

  public Project toProject(ProjectRequest request, Integer userId, String publicId) {
    return Project.builder()
        .userId(userId)
        .publicId(publicId)
        .projectCaption(request.getProjectCaption())
        .description(request.getDescription())
        .status(request.getStatus())
        .build();
  }

}
