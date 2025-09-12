package com.portfolio.taskapp.MyTaskManager.task.service.mapper;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Project;
import com.portfolio.taskapp.MyTaskManager.domain.entity.Task;
import com.portfolio.taskapp.MyTaskManager.task.dto.ProjectRequest;
import com.portfolio.taskapp.MyTaskManager.task.dto.TaskRequest;
import org.springframework.stereotype.Component;

/**
 * プロジェクトおよびタスクの登録・更新のリクエストとその他必要情報からエンティティに変換するマッパークラス。
 */
@Component
public class ProjectTaskMapper {

  /**
   * リクエスト情報とユーザーID、プロジェクトの公開IDを基に Project エンティティを生成するマッパー。
   *
   * @param request  プロジェクト登録用のリクエスト
   * @param userId   内部ユーザーID
   * @param publicId プロジェクトの公開ID
   * @return Project プロジェクトのエンティティ
   */
  public Project toProject(ProjectRequest request, Integer userId, String publicId) {
    return Project.builder()
        .userId(userId)
        .publicId(publicId)
        .projectCaption(request.getProjectCaption())
        .description(request.getDescription())
        .status(request.getStatus())
        .build();
  }

  /**
   * リクエスト情報とプロジェクトID、タスクの公開IDを基に Task エンティティを生成するマッパー。
   * <p>
   * このタスクは親タスクであり、parentTaskId は null になります。
   *
   * @param request  タスク登録・更新用のリクエスト
   * @param project  登録タスクと紐づくプロジェクト
   * @param publicId タスクの公開ID
   * @return Task タスクのエンティティ
   */
  public Task toTask(TaskRequest request, Project project, String publicId) {
    return Task.builder()
        .userAccountId(project.getUserId())
        .projectId(project.getId())
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

  /**
   * リクエスト情報、登録タスクと紐づく親タスク情報、登録タスクの公開IDを基に Task エンティティを生成するマッパー
   * <p>
   * このタスクはサブタスクであり、親タスク情報から parentTaskId が設定されます。
   *
   * @param request    タスク登録・更新用のリクエスト
   * @param parentTask 登録サブタスクと紐づく親タスクのオブジェクト
   * @param publicId   タスクの公開ID
   * @return Task タスクのエンティティ
   */
  public Task toSubtask(TaskRequest request, Task parentTask, String publicId) {
    return Task.builder()
        .userAccountId(parentTask.getUserAccountId())
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

  public Task toUpdateTask(TaskRequest request, Task currentTask) {
    return Task.builder()
        .id(currentTask.getId())
        .userAccountId(currentTask.getUserAccountId())
        .projectId(currentTask.getProjectId())
        .publicId(currentTask.getPublicId())
        .parentTaskId(currentTask.getParentTaskId())
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
