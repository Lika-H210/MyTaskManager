package com.portfolio.taskapp.MyTaskManager.task.service;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Project;
import com.portfolio.taskapp.MyTaskManager.domain.entity.Task;
import com.portfolio.taskapp.MyTaskManager.exception.custom.RecordNotFoundException;
import com.portfolio.taskapp.MyTaskManager.task.dto.ProjectRequest;
import com.portfolio.taskapp.MyTaskManager.task.dto.TaskRequest;
import com.portfolio.taskapp.MyTaskManager.task.dto.TaskTree;
import com.portfolio.taskapp.MyTaskManager.task.repository.TaskRepository;
import com.portfolio.taskapp.MyTaskManager.task.service.converter.TaskConverter;
import com.portfolio.taskapp.MyTaskManager.task.service.mapper.ProjectTaskMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * プロジェクトおよびタスクに関するビジネスロジックを提供するサービスクラス。
 * <p>
 * DBアクセスは TaskRepository を介して行い、<br> 表示用の構造変換には TaskConverter、エンティティとDTO間の変換には ProjectTaskMapper
 * を利用します。<br>
 */
@Service
public class TaskService {

  private final TaskRepository repository;
  private final TaskConverter converter;
  private final ProjectTaskMapper mapper;

  @Autowired
  public TaskService(TaskRepository repository, TaskConverter converter, ProjectTaskMapper mapper) {
    this.repository = repository;
    this.converter = converter;
    this.mapper = mapper;
  }

  /**
   * ユーザーが持つプロジェクト一覧を取得します。
   *
   * @param userPublicId ユーザーの公開ID
   * @return 該当ユーザーに紐づくプロジェクト一覧
   * @throws RecordNotFoundException ユーザーが存在しない場合
   */
  public List<Project> getUserProjects(String userPublicId) {
    Integer userId = requireUserIdByPublicId(userPublicId);
    return repository.findProjectsByUserId(userId);
  }

  /**
   * プロジェクトの公開IDからプロジェクトを取得します。
   *
   * @param projectPublicId プロジェクトの公開ID
   * @param userId          リクエスト送信ユーザーの内部ID
   * @return プロジェクト情報
   * @throws RecordNotFoundException プロジェクトが存在しない場合
   */
  public Project getProjectByProjectPublicId(String projectPublicId, Integer userId) {
    Project project = Optional.ofNullable(repository.findProjectByProjectPublicId(projectPublicId))
        .orElseThrow(() -> new RecordNotFoundException("project not found"));

    // 不正アクセスチェック
    if (!project.getUserId().equals(userId)) {
      // Todo:別途カスタム例外作成し差し替え
      throw new AccessDeniedException("no permission on project");
    }

    return project;
  }

  /**
   * プロジェクトに紐づくタスクを取得し、ツリー形式に変換して返します。
   *
   * @param projectPublicId プロジェクトの公開ID
   * @return 親子タスク一覧
   * @throws RecordNotFoundException プロジェクトが存在しない場合
   */
  public List<TaskTree> getTasksByProjectPublicId(String projectPublicId) {
    Integer projectId = requireProjectIdByPublicId(projectPublicId);

    List<Task> taskList = repository.findTasksByProjectId(projectId);
    return converter.convertToTaskTreeList(taskList);
  }

  /**
   * 公開IDから単一のタスクツリーを取得します。
   *
   * @param taskPublicId タスクの公開ID
   * @return 該当親子タスク情報
   * @throws RecordNotFoundException タスクが存在しない場合
   * @throws IllegalStateException   該当するタスクツリーが1件に特定できない場合
   */
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

  /**
   * 公開IDから単一のタスクを取得します。
   *
   * @param taskPublicId タスクの公開ID
   * @return タスク情報
   * @throws RecordNotFoundException タスクが存在しない場合
   */
  public Task getTaskByTaskPublicId(String taskPublicId) {
    return Optional.ofNullable(repository.findTaskByTaskPublicId(taskPublicId))
        .orElseThrow(() -> new RecordNotFoundException("task not found"));
  }

  /**
   * プロジェクトを新規作成します。
   *
   * @param request      プロジェクト作成リクエスト
   * @param userPublicId ユーザーの公開ID
   * @return 作成されたプロジェクト情報
   * @throws RecordNotFoundException ユーザーが存在しない場合
   */
  @Transactional
  public Project createProject(ProjectRequest request, String userPublicId) {
    Integer userId = requireUserIdByPublicId(userPublicId);

    String publicId = UUID.randomUUID().toString();
    Project project = mapper.toProject(request, userId, publicId);

    repository.createProject(project);

    return project;
  }

  /**
   * プロジェクト直下に親タスクを作成します。
   *
   * @param request         タスク作成リクエスト
   * @param projectPublicId プロジェクトの公開ID
   * @param userId          リクエスト送信ユーザーの内部ID
   * @return 作成されたタスク情報
   * @throws RecordNotFoundException プロジェクトが存在しない場合
   */
  @Transactional
  public Task createParentTask(TaskRequest request, String projectPublicId, Integer userId) {
    Project project = getProjectByProjectPublicId(projectPublicId, userId);

    String publicId = UUID.randomUUID().toString();
    Task task = mapper.toTask(request, project, publicId);

    repository.createTask(task);

    return task;
  }

  /**
   * 指定したタスクの子タスクを作成します。
   *
   * @param request      タスク作成リクエスト
   * @param taskPublicId 親タスクの公開ID
   * @param userId       リクエスト送信ユーザーの内部ID
   * @return 作成された子タスク情報
   * @throws RecordNotFoundException 親タスクが存在しない場合
   */
  @Transactional
  public Task createSubtask(TaskRequest request, String taskPublicId, Integer userId) {
    Task parentTask = Optional.ofNullable(repository.findTaskByTaskPublicId(taskPublicId))
        .orElseThrow(() -> new RecordNotFoundException("parent task not found"));

    // 不正アクセスチェック
    if (!parentTask.getUserAccountId().equals(userId)) {
      // Todo:別途カスタム例外作成し差し替え
      throw new AccessDeniedException("no permission on parent task");
    }

    String publicId = UUID.randomUUID().toString();
    Task task = mapper.toSubtask(request, parentTask, publicId);

    repository.createTask(task);

    return task;
  }

  /**
   * プロジェクト情報を更新します。
   *
   * @param request         プロジェクト更新リクエスト
   * @param projectPublicId プロジェクトの公開ID
   * @return 更新後のプロジェクト情報
   * @throws RecordNotFoundException プロジェクトが存在しない場合
   */
  @Transactional
  public Project updateProject(ProjectRequest request, String projectPublicId) {

    Project project = mapper.toProject(request, null, projectPublicId);
    int updateRows = repository.updateProject(project);

    if (updateRows == 0) {
      throw new RecordNotFoundException("project not found");
    }

    return project;
  }

  /**
   * タスク情報を更新します。
   *
   * @param request      タスク更新リクエスト
   * @param taskPublicId タスクの公開ID
   * @return 更新後のタスク情報
   * @throws RecordNotFoundException タスクが存在しない場合
   */
  @Transactional
  public Task updateTask(TaskRequest request, String taskPublicId) {

    Task task = mapper.toTask(request, null, taskPublicId);
    int updateRows = repository.updateTask(task);

    if (updateRows == 0) {
      throw new RecordNotFoundException("task not found");
    }

    return repository.findTaskByTaskPublicId(taskPublicId);
  }

  /**
   * プロジェクトを削除します。
   *
   * @param projectPublicId プロジェクトの公開ID
   * @throws RecordNotFoundException プロジェクトが存在しない場合
   */
  @Transactional
  public void deleteProject(String projectPublicId) {
    if (repository.findProjectIdByProjectPublicId(projectPublicId) == null) {
      throw new RecordNotFoundException("project not found");
    }
    repository.deleteProject(projectPublicId);
  }

  /**
   * タスクを削除します。
   *
   * @param taskPublicId タスクの公開ID
   * @throws RecordNotFoundException タスクが存在しない場合
   */
  @Transactional
  public void deleteTask(String taskPublicId) {
    if (repository.findTaskIdByTaskPublicId(taskPublicId) == null) {
      throw new RecordNotFoundException("task not found");
    }
    repository.deleteTask(taskPublicId);
  }

  /**
   * 対象の公開IDユーザーの存在確認を行います。
   *
   * @return 該当ユーザーの内部ID
   * @throws RecordNotFoundException ユーザーが存在しない場合
   */
  private Integer requireUserIdByPublicId(String userPublicId) {
    return Optional.ofNullable(repository.findUserIdByUserPublicId(userPublicId))
        .orElseThrow(() -> new RecordNotFoundException("user not found"));
  }

  /**
   * 対象の公開IDプロジェクトの存在確認を行います。
   *
   * @return 該当プロジェクトの内部ID
   * @throws RecordNotFoundException プロジェクトが存在しない場合
   */
  private Integer requireProjectIdByPublicId(String projectPublicId) {
    return Optional.ofNullable(repository.findProjectIdByProjectPublicId(projectPublicId))
        .orElseThrow(() -> new RecordNotFoundException("project not found"));
  }

}
