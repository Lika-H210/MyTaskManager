package com.portfolio.taskapp.MyTaskManager.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

  @Mock
  private TaskRepository repository;

  @Mock
  private TaskConverter converter;

  @Mock
  private ProjectTaskMapper mapper;

  private TaskService sut;

  private static final Integer USER_ID = 999;
  private static final Integer PROJECT_ID = 9999;
  private static final Integer TASK_ID = 99999;
  private static final String USER_PUBLIC_ID = "00000000-0000-0000-0000-000000000000";
  private static final String PROJECT_PUBLIC_ID = "00000000-0000-0000-0000-000000000001";
  private static final String TASK_PUBLIC_ID = "00000000-0000-0000-0000-000000000002";


  @BeforeEach
  void setUp() {
    sut = new TaskService(repository, converter, mapper);
  }

  // ユーザープロジェクト一覧取得：正常系
  @Test
  void ユーザープロジェクトの一覧取得で適切なrepositoryが呼び出せていること() {
    Integer userId = 999;

    when(repository.findUserIdByUserPublicId(USER_PUBLIC_ID)).thenReturn(userId);

    sut.getUserProjects(USER_PUBLIC_ID);

    verify(repository).findUserIdByUserPublicId(USER_PUBLIC_ID);
    verify(repository).findProjectsByUserId(userId);
  }

  // ユーザープロジェクト一覧取得：異常系(404：ユーザーId取得メソッドの例外ルート確認）
  @Test
  void 未登録のユーザー公開IDでプロジェクト一覧取得を実行した場合に例外がThrowされること() {
    when(repository.findUserIdByUserPublicId(USER_PUBLIC_ID)).thenReturn(null);

    assertThatThrownBy(() -> sut.getUserProjects(USER_PUBLIC_ID))
        .isInstanceOf(RecordNotFoundException.class)
        .hasMessageContaining("user not found");

    verify(repository, never()).findProjectsByUserId(any());
  }

  // 単独プロジェクト取得：正常系
  @Test
  void 単独プロジェクト取得で適切なrepositoryが呼び出せていること() {
    Project project = Project.builder()
        .userId(USER_ID)
        .build();
    when(repository.findProjectByProjectPublicId(PROJECT_PUBLIC_ID)).thenReturn(project);

    sut.getProjectByProjectPublicId(PROJECT_PUBLIC_ID, USER_ID);

    verify(repository).findProjectByProjectPublicId(PROJECT_PUBLIC_ID);
  }

  // 親子タスク一覧取得：正常系
  @Test
  void プロジェクトに紐づく親子タスク一覧取得時に適切なrepositoryとconverterが呼び出せていること() {
    Project project = Project.builder()
        .id(PROJECT_ID)
        .userId(USER_ID)
        .build();
    List<Task> taskList = List.of();

    when(repository.findProjectByProjectPublicId(PROJECT_PUBLIC_ID)).thenReturn(project);
    when(repository.findTasksByProjectId(PROJECT_ID)).thenReturn(taskList);

    sut.getTasksByProjectPublicId(PROJECT_PUBLIC_ID, USER_ID);

    verify(repository).findProjectByProjectPublicId(PROJECT_PUBLIC_ID);
    verify(repository).findTasksByProjectId(PROJECT_ID);
    verify(converter).convertToTaskTreeList(taskList);
  }

  // 単独親子タスク取得：正常系
  @Test
  void 単独の親子タスク取得する際に必要なrepositoryとconverterが呼び出され単独親子タスクが返されていること() {
    // 事前準備
    Task parentTask = Task.builder()
        .id(TASK_ID)
        .userAccountId(USER_ID)
        .build();
    List<Task> taskList = List.of();
    TaskTree taskTree = new TaskTree();
    List<TaskTree> taskTreesList = List.of(taskTree);

    when(repository.findTaskByTaskPublicId(TASK_PUBLIC_ID)).thenReturn(parentTask);
    when(repository.findTasksByTaskId(TASK_ID)).thenReturn(taskList);
    when(converter.convertToTaskTreeList(taskList)).thenReturn(taskTreesList);

    // 実行
    TaskTree actual = sut.getTaskTreeByTaskPublicId(TASK_PUBLIC_ID, USER_ID);

    // 検証
    verify(repository).findTaskByTaskPublicId(TASK_PUBLIC_ID);
    verify(repository).findTasksByTaskId(TASK_ID);
    verify(converter).convertToTaskTreeList(taskList);

    assertThat(actual).isEqualTo(taskTree);
  }

  // 単独親子タスク取得：異常系(500)　※convert結果が複数要素のリストの場合
  @Test
  void 単独の親子タスク取得する際にconvert処理で複数要素のリストが返った場合に適切な例外がThrowされること() {
    // 事前準備
    Task parentTask = Task.builder()
        .id(TASK_ID)
        .userAccountId(USER_ID)
        .build();
    List<TaskTree> taskTreesList = List.of(new TaskTree(), new TaskTree());

    when(repository.findTaskByTaskPublicId(TASK_PUBLIC_ID)).thenReturn(parentTask);
    when(converter.convertToTaskTreeList(anyList())).thenReturn(taskTreesList);

    assertThatThrownBy(() -> sut.getTaskTreeByTaskPublicId(TASK_PUBLIC_ID, USER_ID))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("TaskTree count mismatch");
  }

  // 単独タスク取得：正常系
  @Test
  void 単独タスク取得で適切なrepositoryが呼び出せていること() {
    Task task = Task.builder()
        .userAccountId(USER_ID)
        .build();
    when(repository.findTaskByTaskPublicId(TASK_PUBLIC_ID)).thenReturn(task);

    sut.getTaskByTaskPublicId(TASK_PUBLIC_ID, USER_ID);

    verify(repository).findTaskByTaskPublicId(TASK_PUBLIC_ID);
  }

  // プロジェクト登録処理：正常系
  @Test
  void プロジェクト登録処理で適切なrepositoryとmapperが呼び出されていること() {
    Integer userId = 999;
    ProjectRequest request = new ProjectRequest();
    Project project = new Project();

    when(repository.findUserIdByUserPublicId(USER_PUBLIC_ID)).thenReturn(userId);
    when(mapper.toProject(eq(request), eq(userId), any(String.class))).thenReturn(project);

    Project actual = sut.createProject(request, USER_PUBLIC_ID);

    verify(repository).findUserIdByUserPublicId(USER_PUBLIC_ID);
    verify(mapper).toProject(eq(request), eq(userId), any(String.class));
    verify(repository).createProject(project);

    assertThat(actual).isEqualTo(project);
  }

  // 親タスク登録処理：正常系
  @Test
  void 親タスク登録処理で適切なrepositoryとmapperが呼び出されていること() {
    Project project = Project.builder()
        .userId(USER_ID)
        .build();
    TaskRequest request = new TaskRequest();
    Task task = new Task();

    when(repository.findProjectByProjectPublicId(PROJECT_PUBLIC_ID)).thenReturn(project);
    when(mapper.toTask(eq(request), eq(project), any(String.class))).thenReturn(task);

    Task actual = sut.createParentTask(request, PROJECT_PUBLIC_ID, USER_ID);

    verify(repository).findProjectByProjectPublicId(PROJECT_PUBLIC_ID);
    verify(mapper).toTask(eq(request), eq(project), any(String.class));
    verify(repository).createTask(task);

    assertThat(actual).isEqualTo(task);
  }

  // 子タスク登録処理：正常系
  @Test
  void 子タスク登録処理で適切なrepositoryとmapperが呼び出され更新内容のタスクが返されていること() {
    Integer projectId = 9999;
    Integer taskId = 99999;
    Task parentTask = Task.builder()
        .id(taskId)
        .userAccountId(USER_ID)
        .projectId(projectId)
        .build();
    TaskRequest request = new TaskRequest();
    Task task = new Task();

    when(repository.findTaskByTaskPublicId(TASK_PUBLIC_ID)).thenReturn(parentTask);
    when(mapper.toSubtask(eq(request), eq(parentTask), anyString())).thenReturn(task);

    Task actual = sut.createSubtask(request, TASK_PUBLIC_ID, USER_ID);

    verify(repository).findTaskByTaskPublicId(TASK_PUBLIC_ID);
    verify(mapper).toSubtask(eq(request), eq(parentTask), anyString());
    verify(repository).createTask(task);

    assertThat(actual).isEqualTo(task);
  }

  // プロジェクト更新処理：正常系
  @Test
  void プロジェクト更新処理で適切なrepositoryとmapperが呼び出されていること() {
    Project currentProject = Project.builder()
        .userId(USER_ID)
        .publicId(PROJECT_PUBLIC_ID)
        .build();
    ProjectRequest request = new ProjectRequest();
    Project updateProject = new Project();

    when(repository.findProjectByProjectPublicId(PROJECT_PUBLIC_ID)).thenReturn(currentProject);
    when(mapper.toProject(request, USER_ID, PROJECT_PUBLIC_ID)).thenReturn(updateProject);

    sut.updateProject(request, PROJECT_PUBLIC_ID, USER_ID);

    verify(repository).findProjectByProjectPublicId(PROJECT_PUBLIC_ID);
    verify(mapper).toProject(request, USER_ID, PROJECT_PUBLIC_ID);
    verify(repository).updateProject(updateProject);
  }

  // タスク更新処理:正常系
  @Test
  void タスク更新処理で適切なrepositoryとmapperが呼び出されていること() {
    TaskRequest request = new TaskRequest();
    Task currentTask = Task.builder()
        .userAccountId(USER_ID)
        .build();
    Task task = new Task();

    when(repository.findTaskByTaskPublicId(TASK_PUBLIC_ID)).thenReturn(currentTask);
    when(mapper.toUpdateTask(request, currentTask)).thenReturn(task);

    sut.updateTask(request, TASK_PUBLIC_ID, USER_ID);

    verify(repository).findTaskByTaskPublicId(TASK_PUBLIC_ID);
    verify(mapper).toUpdateTask(request, currentTask);
    verify(repository).updateTask(task);
  }

  // プロジェクト削除処理：正常系
  @Test
  void プロジェクト削除処理で適切なrepositoryが呼び出されていること() {
    Project project = Project.builder()
        .userId(USER_ID)
        .build();

    when(repository.findProjectByProjectPublicId(PROJECT_PUBLIC_ID)).thenReturn(project);

    sut.deleteProject(PROJECT_PUBLIC_ID, USER_ID);

    verify(repository).findProjectByProjectPublicId(PROJECT_PUBLIC_ID);
    verify(repository).deleteProject(PROJECT_PUBLIC_ID);
  }

  // タスク削除処理：正常系
  @Test
  void タスク削除処理で適切なrepositoryが呼び出されていること() {
    Task task = Task.builder()
        .userAccountId(USER_ID)
        .build();

    when(repository.findTaskByTaskPublicId(TASK_PUBLIC_ID)).thenReturn(task);

    sut.deleteTask(TASK_PUBLIC_ID, USER_ID);

    verify(repository).findTaskByTaskPublicId(TASK_PUBLIC_ID);
    verify(repository).deleteTask(TASK_PUBLIC_ID);
  }

  // プロジェクト存在確認＆所有検証：正常系
  @Test
  void プロジェクト存在確認において適切なrepositoryを呼び出しプロジェクトを返していること() {
    Project project = Project.builder()
        .userId(USER_ID)
        .build();
    when(repository.findProjectByProjectPublicId(PROJECT_PUBLIC_ID)).thenReturn(project);

    Project actual = sut.getAuthorizedProject(PROJECT_PUBLIC_ID, USER_ID);

    verify(repository).findProjectByProjectPublicId(PROJECT_PUBLIC_ID);
    assertThat(actual).isEqualTo(project);
  }

  // プロジェクト存在確認＆所有検証：異常系：404
  @Test
  void プロジェクト存在確認においてnullであった場合に適切な例外がThrowされること() {
    when(repository.findProjectByProjectPublicId(PROJECT_PUBLIC_ID)).thenReturn(null);

    assertThatThrownBy(() -> sut.getAuthorizedProject(PROJECT_PUBLIC_ID, USER_ID))
        .isInstanceOf(RecordNotFoundException.class)
        .hasMessage("project not found");
  }

  // プロジェクト存在確認＆所有検証：異常系：403
  @Test
  void プロジェクト所有検証において所有者が異なる場合に適切な例外がThrowされること() {
    Project project = Project.builder()
        .userId(1000)
        .build();
    when(repository.findProjectByProjectPublicId(PROJECT_PUBLIC_ID)).thenReturn(project);

    assertThatThrownBy(() -> sut.getAuthorizedProject(PROJECT_PUBLIC_ID, USER_ID))
        .isInstanceOf(InvalidOwnerAccessException.class)
        .satisfies(ex -> {
          InvalidOwnerAccessException e = (InvalidOwnerAccessException) ex;
          assertThat(e.getTargetResource()).isEqualTo(TargetResource.PROJECT);
        });
  }

  // タスク存在確認＆所有検証：正常系
  @Test
  void タスク存在確認において適切なrepositoryを呼び出しタスクを返していること() {
    Task task = Task.builder()
        .userAccountId(USER_ID)
        .build();
    when(repository.findTaskByTaskPublicId(TASK_PUBLIC_ID)).thenReturn(task);

    Task actual = sut.getAuthorizedTask(TASK_PUBLIC_ID, USER_ID);

    verify(repository).findTaskByTaskPublicId(TASK_PUBLIC_ID);
    assertThat(actual).isEqualTo(task);
  }

  // タスク存在確認＆所有検証：異常系：404
  @Test
  void タスク存在確認においてnullであった場合に適切な例外がThrowされること() {
    when(repository.findTaskByTaskPublicId(TASK_PUBLIC_ID)).thenReturn(null);

    assertThatThrownBy(() -> sut.getAuthorizedTask(TASK_PUBLIC_ID, USER_ID))
        .isInstanceOf(RecordNotFoundException.class)
        .hasMessage("task not found");
  }

  // タスク存在確認＆所有検証：異常系：403
  @Test
  void タスク所有検証において所有者が異なる場合に適切な例外がThrowされること() {
    Task task = Task.builder()
        .userAccountId(1000)
        .build();
    when(repository.findTaskByTaskPublicId(TASK_PUBLIC_ID)).thenReturn(task);

    assertThatThrownBy(() -> sut.getAuthorizedTask(TASK_PUBLIC_ID, USER_ID))
        .isInstanceOf(InvalidOwnerAccessException.class)
        .satisfies(ex -> {
          InvalidOwnerAccessException e = (InvalidOwnerAccessException) ex;
          assertThat(e.getTargetResource()).isEqualTo(TargetResource.TASK);
        });
  }
}
