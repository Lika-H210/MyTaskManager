package com.portfolio.taskapp.MyTaskManager.task.service;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Project;
import com.portfolio.taskapp.MyTaskManager.domain.entity.Task;
import com.portfolio.taskapp.MyTaskManager.exception.custom.InvalidOwnerAccessException;
import com.portfolio.taskapp.MyTaskManager.exception.custom.RecordNotFoundException;
import com.portfolio.taskapp.MyTaskManager.exception.custom.enums.TargetResource;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * プロジェクトおよびタスクに関するビジネスロジックを提供するサービスクラス。
 * <p>
 * DBアクセスは TaskRepository を介して行い、表示用の構造変換には TaskConverter、エンティティとDTO間の変換には ProjectTaskMapper
 * を利用します。
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
   * <p>
   * プロジェクトが存在しない場合、または取得プロジェクトがリクエストユーザーに属していない場合は例外を送出します。
   *
   * @param projectPublicId プロジェクトの公開ID
   * @param userId          リクエスト送信ユーザーの内部ID
   * @return プロジェクト情報
   * @throws RecordNotFoundException     プロジェクトが存在しない場合
   * @throws InvalidOwnerAccessException 呼び出し元ユーザーが指定されたプロジェクトの所有者でない場合
   */
  public Project getProjectByProjectPublicId(String projectPublicId, Integer userId) {
    return getAuthorizedProject(projectPublicId, userId);
  }

  /**
   * プロジェクトに紐づくタスクを取得し、ツリー形式に変換して返します。
   * <p>
   * プロジェクトが存在しない場合、または取得プロジェクトがリクエストユーザーに属していない場合は例外を送出します。
   *
   * @param projectPublicId プロジェクトの公開ID
   * @param userId          リクエスト送信ユーザーの内部ID
   * @return 親子タスク一覧
   * @throws RecordNotFoundException     プロジェクトが存在しない場合
   * @throws InvalidOwnerAccessException 呼び出し元ユーザーが指定されたプロジェクトの所有者でない場合
   */
  public List<TaskTree> getTasksByProjectPublicId(String projectPublicId, Integer userId) {
    Project project = getAuthorizedProject(projectPublicId, userId);

    List<Task> taskList = repository.findTasksByProjectId(project.getId());
    return converter.convertToTaskTreeList(taskList);
  }

  /**
   * 公開IDから1件のタスクツリーを取得します。
   * <p>
   * タスクが存在しない場合、または取得タスクがリクエストユーザーに属していない場合は例外を送出します。
   *
   * @param taskPublicId 親タスクの公開ID
   * @param userId       リクエスト送信ユーザーの内部ID
   * @return 該当親子タスク情報
   * @throws RecordNotFoundException     親タスクが存在しない場合
   * @throws IllegalStateException       該当するタスクツリーが1件に特定できない場合
   * @throws InvalidOwnerAccessException 呼び出し元ユーザーが指定された親タスクの所有者でない場合
   */
  public TaskTree getTaskTreeByTaskPublicId(String taskPublicId, Integer userId) {
    Task parentTask = getAuthorizedTask(taskPublicId, userId);

    List<Task> taskList = repository.findTasksByTaskId(parentTask.getId());
    List<TaskTree> taskTreeList = converter.convertToTaskTreeList(taskList);

    if (taskTreeList.size() != 1) {
      throw new IllegalStateException(
          "TaskTree count mismatch: expected 1, but got " + taskTreeList.size());
    }
    return taskTreeList.getFirst();
  }

  /**
   * 公開IDから単一のタスクを取得します。
   * <p>
   * タスクが存在しない場合、または取得タスクがリクエストユーザーに属していない場合は例外を送出します。
   *
   * @param taskPublicId タスクの公開ID
   * @param userId       リクエスト送信ユーザーの内部ID
   * @return タスク情報
   * @throws RecordNotFoundException     タスクが存在しない場合
   * @throws InvalidOwnerAccessException 呼び出し元ユーザーが指定されたタスクの所有者でない場合
   */
  public Task getTaskByTaskPublicId(String taskPublicId, Integer userId) {
    return getAuthorizedTask(taskPublicId, userId);
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
   * <p>
   * 紐づけるプロジェクトが存在しない場合、または取得プロジェクトがリクエストユーザーに属していない場合は例外を送出します。
   *
   * @param request         タスク作成リクエスト
   * @param projectPublicId プロジェクトの公開ID
   * @param userId          リクエスト送信ユーザーの内部ID
   * @return 作成されたタスク情報
   * @throws RecordNotFoundException     プロジェクトが存在しない場合
   * @throws InvalidOwnerAccessException 呼び出し元ユーザーが指定されたプロジェクトの所有者でない場合
   */
  @Transactional
  public Task createParentTask(TaskRequest request, String projectPublicId, Integer userId) {
    Project project = getAuthorizedProject(projectPublicId, userId);

    String publicId = UUID.randomUUID().toString();
    Task task = mapper.toTask(request, project, publicId);

    repository.createTask(task);

    return task;
  }

  /**
   * 指定したタスクの子タスクを作成します。
   * <p>
   * 紐づける親タスクが存在しない場合、または取得親タスクがリクエストユーザーに属していない場合は例外を送出します。
   *
   * @param request      タスク作成リクエスト
   * @param taskPublicId 親タスクの公開ID
   * @param userId       リクエスト送信ユーザーの内部ID
   * @return 作成された子タスク情報
   * @throws RecordNotFoundException     親タスクが存在しない場合
   * @throws InvalidOwnerAccessException 呼び出し元ユーザーが指定された親タスクの所有者でない場合
   */
  @Transactional
  public Task createSubtask(TaskRequest request, String taskPublicId, Integer userId) {
    Task parentTask = getAuthorizedTask(taskPublicId, userId);

    String publicId = UUID.randomUUID().toString();
    Task task = mapper.toSubtask(request, parentTask, publicId);

    repository.createTask(task);

    return task;
  }

  /**
   * プロジェクト情報を更新します。
   * <p>
   * 更新対象のプロジェクトが存在しない場合、またはプロジェクトがリクエストユーザーに属していない場合は例外を送出します。
   *
   * @param request         プロジェクト更新リクエスト
   * @param projectPublicId プロジェクトの公開ID
   * @param userId          リクエスト送信ユーザーの内部ID
   * @return 更新後のプロジェクト情報
   * @throws RecordNotFoundException     プロジェクトが存在しない場合
   * @throws InvalidOwnerAccessException 呼び出し元ユーザーが指定されたプロジェクトの所有者でない場合
   */
  @Transactional
  public Project updateProject(ProjectRequest request, String projectPublicId, Integer userId) {
    Project currentProject = getAuthorizedProject(projectPublicId, userId);

    Project updateProject = mapper.toProject(request, currentProject.getUserId(), projectPublicId);
    repository.updateProject(updateProject);

    return updateProject;
  }

  /**
   * タスク情報を更新します。
   * <p>
   * 更新対象のタスクが存在しない場合、またはタスクがリクエストユーザーに属していない場合は例外を送出します。
   *
   * @param request      タスク更新リクエスト
   * @param taskPublicId タスクの公開ID
   * @param userId       リクエスト送信ユーザーの内部ID
   * @return 更新後のタスク情報
   * @throws RecordNotFoundException     タスクが存在しない場合
   * @throws InvalidOwnerAccessException 呼び出し元ユーザーが指定されたタスクの所有者でない場合
   */
  @Transactional
  public Task updateTask(TaskRequest request, String taskPublicId, Integer userId) {
    Task currentTask = getAuthorizedTask(taskPublicId, userId);

    Task updateTask = mapper.toUpdateTask(request, currentTask);
    repository.updateTask(updateTask);

    return updateTask;
  }

  /**
   * プロジェクトを削除します。
   * <p>
   * 削除対象のプロジェクトが存在しない場合、またはプロジェクトがリクエストユーザーに属していない場合は例外を送出します。
   *
   * @param projectPublicId プロジェクトの公開ID
   * @param userId          リクエスト送信ユーザーの内部ID
   * @throws RecordNotFoundException     プロジェクトが存在しない場合
   * @throws InvalidOwnerAccessException 呼び出し元ユーザーが指定されたプロジェクトの所有者でない場合
   */
  @Transactional
  public void deleteProject(String projectPublicId, Integer userId) {
    getAuthorizedProject(projectPublicId, userId);

    repository.deleteProject(projectPublicId);
  }

  /**
   * タスクを削除します。
   * <p>
   * 削除対象のタスクが存在しない場合、またはタスクがリクエストユーザーに属していない場合は例外を送出します。
   *
   * @param taskPublicId タスクの公開ID
   * @param userId       リクエスト送信ユーザーの内部ID
   * @throws RecordNotFoundException     タスクが存在しない場合
   * @throws InvalidOwnerAccessException 呼び出し元ユーザーが指定されたタスクの所有者でない場合
   */
  @Transactional
  public void deleteTask(String taskPublicId, Integer userId) {
    getAuthorizedTask(taskPublicId, userId);
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

  Project getAuthorizedProject(String projectPublicId, Integer userId) {
    Project project = Optional.ofNullable(repository.findProjectByProjectPublicId(projectPublicId))
        .orElseThrow(() -> new RecordNotFoundException("project not found"));

    // 不正アクセスチェック
    if (!project.getUserId().equals(userId)) {
      throw new InvalidOwnerAccessException(TargetResource.PROJECT);
    }
    return project;
  }

  Task getAuthorizedTask(String taskPublicId, Integer userId) {
    Task task = Optional.ofNullable(repository.findTaskByTaskPublicId(taskPublicId))
        .orElseThrow(() -> new RecordNotFoundException("task not found"));

    // 不正アクセスチェック
    if (!task.getUserAccountId().equals(userId)) {
      throw new InvalidOwnerAccessException(TargetResource.TASK);
    }
    return task;
  }

}
