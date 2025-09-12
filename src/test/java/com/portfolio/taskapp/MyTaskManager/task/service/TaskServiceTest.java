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
import com.portfolio.taskapp.MyTaskManager.exception.custom.RecordNotFoundException;
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
    Project project = new Project();
    when(repository.findProjectByProjectPublicId(PROJECT_PUBLIC_ID)).thenReturn(project);

    sut.getProjectByProjectPublicId(PROJECT_PUBLIC_ID, USER_ID);

    verify(repository).findProjectByProjectPublicId(PROJECT_PUBLIC_ID);
  }

  // 単独プロジェクト取得：異常系
  @Test
  void 単独プロジェクト取得でプロジェクトが存在しない場合に例外がThrowされること() {
    when(repository.findProjectByProjectPublicId(PROJECT_PUBLIC_ID)).thenReturn(null);

    assertThatThrownBy(() -> sut.getProjectByProjectPublicId(PROJECT_PUBLIC_ID, USER_ID))
        .isInstanceOf(RecordNotFoundException.class)
        .hasMessage("project not found");
  }

  // 親子タスク一覧取得：正常系
  @Test
  void プロジェクトに紐づく親子タスク一覧取得時に適切なrepositoryとconverterが呼び出せていること() {
    Integer projectId = 9999;
    List<Task> taskList = List.of();

    when(repository.findProjectIdByProjectPublicId(PROJECT_PUBLIC_ID)).thenReturn(projectId);
    when(repository.findTasksByProjectId(projectId)).thenReturn(taskList);

    sut.getTasksByProjectPublicId(PROJECT_PUBLIC_ID);

    verify(repository).findProjectIdByProjectPublicId(PROJECT_PUBLIC_ID);
    verify(repository).findTasksByProjectId(projectId);
    verify(converter).convertToTaskTreeList(taskList);
  }

  // 親子タスク一覧取得：異常系(404：プロジェクトId取得メソッドの例外ルート確認)
  @Test
  void プロジェクトに紐づく親子タスク一覧取得時に紐づけるタスク情報が取得できなかった場合例外をthrowすること() {
    when(repository.findProjectIdByProjectPublicId(PROJECT_PUBLIC_ID)).thenReturn(null);

    assertThatThrownBy(() -> sut.getTasksByProjectPublicId(PROJECT_PUBLIC_ID))
        .isInstanceOf(RecordNotFoundException.class)
        .hasMessageContaining("project not found");

    verify(repository, never()).findTasksByProjectId(any());
  }

  // 単独親子タスク取得：正常系
  @Test
  void 単独の親子タスク取得する際に必要なrepositoryとconverterが呼び出され単独親子タスクが返されていること() {
    // 事前準備
    Integer taskId = 99999;
    List<Task> taskList = List.of();
    TaskTree taskTree = new TaskTree();
    List<TaskTree> taskTreesList = List.of(taskTree);

    when(repository.findTaskIdByTaskPublicId(TASK_PUBLIC_ID)).thenReturn(taskId);
    when(repository.findTasksByTaskId(taskId)).thenReturn(taskList);
    when(converter.convertToTaskTreeList(taskList)).thenReturn(taskTreesList);

    // 実行
    TaskTree actual = sut.getTaskTreeByTaskPublicId(TASK_PUBLIC_ID);

    // 検証
    verify(repository).findTaskIdByTaskPublicId(TASK_PUBLIC_ID);
    verify(repository).findTasksByTaskId(taskId);
    verify(converter).convertToTaskTreeList(taskList);

    assertThat(actual).isEqualTo(taskTree);
  }

  // 単独親子タスク取得：異常系(404)
  @Test
  void 単独の親子タスク取得で取得対象タスクの内部Idを取得できない場合に適切な例外がThrowされること() {
    when(repository.findTaskIdByTaskPublicId(TASK_PUBLIC_ID)).thenReturn(null);

    assertThatThrownBy(() -> sut.getTaskTreeByTaskPublicId(TASK_PUBLIC_ID))
        .isInstanceOf(RecordNotFoundException.class)
        .hasMessageContaining("task not found");

    verify(repository, never()).findTasksByTaskId(any());
  }

  // 単独親子タスク取得：異常系(500)　※convert結果が複数要素のリストの場合
  @Test
  void 単独の親子タスク取得する際にconvert処理で複数要素のリストが返った場合に適切な例外がThrowされること() {
    // 事前準備
    Integer taskId = 99999;
    List<TaskTree> taskTreesList = List.of(new TaskTree(), new TaskTree());

    when(repository.findTaskIdByTaskPublicId(TASK_PUBLIC_ID)).thenReturn(taskId);
    when(converter.convertToTaskTreeList(anyList())).thenReturn(taskTreesList);

    assertThatThrownBy(() -> sut.getTaskTreeByTaskPublicId(TASK_PUBLIC_ID))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("TaskTree count mismatch");
  }

  // 単独タスク取得：正常系
  @Test
  void 単独タスク取得で適切なrepositoryが呼び出せていること() {
    Task task = new Task();
    when(repository.findTaskByTaskPublicId(TASK_PUBLIC_ID)).thenReturn(task);

    sut.getTaskByTaskPublicId(TASK_PUBLIC_ID);

    verify(repository).findTaskByTaskPublicId(TASK_PUBLIC_ID);
  }

  // 単独タスク取得：異常系
  @Test
  void 単独タスク取得でタスクが存在しない場合に例外がThrowされること() {
    when(repository.findTaskByTaskPublicId(TASK_PUBLIC_ID)).thenReturn(null);

    assertThatThrownBy(() -> sut.getTaskByTaskPublicId(TASK_PUBLIC_ID))
        .isInstanceOf(RecordNotFoundException.class)
        .hasMessage("task not found");
  }

  // プロジェクト登録処理
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

  // 子タスク登録処理：異常系(404)
  @Test
  void 子タスク登録処理でタスク公開Idに紐づくタスク情報を取得できなかった場合に適切な例外がThrowされること() {
    TaskRequest request = new TaskRequest();

    when(repository.findTaskByTaskPublicId(TASK_PUBLIC_ID)).thenReturn(null);

    assertThatThrownBy(() -> sut.createSubtask(request, TASK_PUBLIC_ID, USER_ID))
        .isInstanceOf(RecordNotFoundException.class)
        .hasMessageContaining("task not found");

    verify(mapper, never()).toSubtask(any(), any(), any());
  }

  // プロジェクト更新処理：正常系
  @Test
  void プロジェクト更新処理で適切なrepositoryとmapperが呼び出されていること() {
    ProjectRequest request = new ProjectRequest();
    Project project = new Project();

    when(mapper.toProject(request, null, PROJECT_PUBLIC_ID)).thenReturn(project);
    when(repository.updateProject(project)).thenReturn(1);

    sut.updateProject(request, PROJECT_PUBLIC_ID);

    verify(mapper).toProject(request, null, PROJECT_PUBLIC_ID);
    verify(repository).updateProject(project);
  }

  // プロジェクト更新処理：異常系(404:更新対象のレコードがない場合)
  @Test
  void プロジェクト更新で更新対象のレコードがない場合に例外がthrowされること() {
    ProjectRequest request = new ProjectRequest();
    Project project = new Project();

    when(mapper.toProject(request, null, PROJECT_PUBLIC_ID)).thenReturn(project);
    when(repository.updateProject(project)).thenReturn(0);

    assertThatThrownBy(() -> sut.updateProject(request, PROJECT_PUBLIC_ID))
        .isInstanceOf(RecordNotFoundException.class)
        .hasMessage("project not found");
  }

  // タスク更新処理
  @Test
  void タスク更新処理で適切なrepositoryとmapperが呼び出されていること() {
    TaskRequest request = new TaskRequest();
    Task currentTask = Task.builder()
        .userAccountId(USER_ID)
        .build();
    Task updateTask = new Task();

    when(repository.findTaskByTaskPublicId(TASK_PUBLIC_ID)).thenReturn(currentTask);
    when(mapper.toUpdateTask(request, currentTask)).thenReturn(updateTask);

    sut.updateTask(request, TASK_PUBLIC_ID, USER_ID);

    verify(repository).findTaskByTaskPublicId(TASK_PUBLIC_ID);
    verify(mapper).toUpdateTask(request, currentTask);
    verify(repository).updateTask(updateTask);
  }

  @Test
  void タスク更新処理で更新対象のレコードがない場合に例外がthrowされること() {
    TaskRequest request = new TaskRequest();

    when(repository.findTaskByTaskPublicId(TASK_PUBLIC_ID)).thenReturn(null);

    assertThatThrownBy(() -> sut.updateTask(request, TASK_PUBLIC_ID, USER_ID))
        .isInstanceOf(RecordNotFoundException.class)
        .hasMessage("task not found");

    verify(mapper, never()).toUpdateTask(any(), any());
  }

}
