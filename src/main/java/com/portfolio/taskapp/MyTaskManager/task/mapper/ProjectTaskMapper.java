package com.portfolio.taskapp.MyTaskManager.task.mapper;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Project;
import com.portfolio.taskapp.MyTaskManager.domain.entity.Task;
import com.portfolio.taskapp.MyTaskManager.task.dto.ProjectRequest;
import com.portfolio.taskapp.MyTaskManager.task.dto.TaskRequest;
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

  public Task toTask(TaskRequest request, Integer projectId, String publicId) {
    return Task.builder()
        .projectId(projectId)
        .publicId(publicId)
        .parentTaskId(null)
        .taskCaption(request.getTaskCaption())
        .description(request.getDescription())
        .dueDate(request.getDueDate())
        .estimatedTime(request.getEstimatedTime())
        .actualTime(request.getActualTime())
        .progress(request.getProgress())
        .priority(request.getPriority())
        .build();
  }

  public Task toSubtask(TaskRequest request, Task parentTask, String publicId) {
    return Task.builder()
        .projectId(parentTask.getProjectId())
        .publicId(publicId)
        .parentTaskId(parentTask.getId())
        .taskCaption(request.getTaskCaption())
        .description(request.getDescription())
        .dueDate(request.getDueDate())
        .estimatedTime(request.getEstimatedTime())
        .actualTime(request.getActualTime())
        .progress(request.getProgress())
        .priority(request.getPriority())
        .build();
  }

}
