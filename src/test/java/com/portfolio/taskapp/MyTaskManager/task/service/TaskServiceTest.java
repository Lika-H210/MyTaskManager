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
import com.portfolio.taskapp.MyTaskManager.exception.RecordNotFoundException;
import com.portfolio.taskapp.MyTaskManager.task.mapper.ProjectTaskMapper;
import com.portfolio.taskapp.MyTaskManager.task.model.ProjectRequest;
import com.portfolio.taskapp.MyTaskManager.task.model.TaskRequest;
import com.portfolio.taskapp.MyTaskManager.task.model.TaskTree;
import com.portfolio.taskapp.MyTaskManager.task.repository.TaskRepository;
import com.portfolio.taskapp.MyTaskManager.task.service.converter.TaskConverter;
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

  private static final String USER_PUBLIC_ID = "00000000-0000-0000-0000-000000000000";
  private static final String PROJECT_PUBLIC_ID = "00000000-0000-0000-0000-000000000001";
  private static final String TASK_PUBLIC_ID = "00000000-0000-0000-0000-000000000002";


  @BeforeEach
  void setUp() {
    sut = new TaskService(repository, converter, mapper);
  }

  @Test
  void ユーザープロジェクトの一覧取得で適切なrepositoryが呼び出せていること() {
    Integer userId = 999;

    when(repository.findUserIdByUserPublicId(USER_PUBLIC_ID)).thenReturn(userId);

    sut.getUserProjects(USER_PUBLIC_ID);

    verify(repository).findUserIdByUserPublicId(USER_PUBLIC_ID);
    verify(repository).findProjectsByUserId(userId);
  }

  @Test
  void 未登録のユーザー公開IDでプロジェクト一覧取得を実行すた場合に早期リターンで空のリストが返されること() {
    when(repository.findUserIdByUserPublicId(USER_PUBLIC_ID)).thenReturn(null);

    assertThatThrownBy(() -> sut.getUserProjects(USER_PUBLIC_ID))
        .isInstanceOf(RecordNotFoundException.class)
        .hasMessage("Authenticated user not found in database");

    verify(repository, never()).findProjectsByUserId(any());
  }

  @Test
  void プロジェクトに紐づくタスク取得時に適切なrepositoryとconverterが呼び出せていること() {
    Integer projectId = 9999;

    List<Task> taskList = List.of();

    when(repository.findProjectIdByProjectPublicId(PROJECT_PUBLIC_ID)).thenReturn(projectId);
    when(repository.findTasksByProjectId(projectId)).thenReturn(taskList);

    sut.getTasksByProjectPublicId(PROJECT_PUBLIC_ID);

    verify(repository).findProjectIdByProjectPublicId(PROJECT_PUBLIC_ID);
    verify(repository).findTasksByProjectId(projectId);
    verify(converter).convertToTaskTreeList(taskList);
  }

  // 正常系
  @Test
  void 単独の親子タスク取得する際に必要なrepositoryとconverterが呼び出されていること() {
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

  // 異常系；convert結果が複数要素のリストの場合(repositoryのスタブは正常系でテスト済みのため省略)
  @Test
  void 単独の親子タスク取得する際にconvert処理で複数要素のリストが返った場合に例外処理が実行されること() {
    // 事前準備
    List<TaskTree> taskTreesList = List.of(new TaskTree(), new TaskTree());

    when(converter.convertToTaskTreeList(anyList())).thenReturn(taskTreesList);

    assertThatThrownBy(() -> sut.getTaskTreeByTaskPublicId(TASK_PUBLIC_ID))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("TaskTree count mismatch");
  }

  // プロジェクト登録処理
  @Test
  void プロジェクト登録処理で適切なrepositoryとmapperが呼び出されていること() {
    Integer userId = 999;
    ProjectRequest request = new ProjectRequest();
    Project project = new Project();

    when(repository.findUserIdByUserPublicId(USER_PUBLIC_ID)).thenReturn(userId);
    when(mapper.toProject(eq(request), eq(userId), any(String.class))).thenReturn(project);

    sut.createProject(request, USER_PUBLIC_ID);

    verify(repository).findUserIdByUserPublicId(USER_PUBLIC_ID);
    verify(mapper).toProject(eq(request), eq(userId), any(String.class));
    verify(repository).createProject(project);
  }

  @Test
  void 親タスク登録処理で適切なrepositoryとmapperが呼び出されていること() {
    Integer projectId = 9999;
    TaskRequest request = new TaskRequest();
    Task task = new Task();

    when(repository.findProjectIdByProjectPublicId(PROJECT_PUBLIC_ID)).thenReturn(projectId);
    when(mapper.toTask(eq(request), eq(projectId), any(String.class))).thenReturn(task);

    sut.createParentTask(request, PROJECT_PUBLIC_ID);

    verify(repository).findProjectIdByProjectPublicId(PROJECT_PUBLIC_ID);
    verify(mapper).toTask(eq(request), eq(projectId), any(String.class));
    verify(repository).createTask(task);
  }

  @Test
  void 子タスク登録処理で適切なrepositoryとmapperが呼び出されていること() {
    Integer taskId = 99999;
    Integer projectId = 9999;
    Task parentTask = Task.builder()
        .id(taskId)
        .projectId(projectId)
        .build();
    TaskRequest request = new TaskRequest();
    Task task = new Task();

    when(repository.findTaskByTaskPublicId(TASK_PUBLIC_ID)).thenReturn(parentTask);
    when(mapper.toSubtask(eq(request), eq(parentTask), anyString())).thenReturn(task);

    sut.createSubtask(request, TASK_PUBLIC_ID);

    verify(repository).findTaskByTaskPublicId(TASK_PUBLIC_ID);
    verify(mapper).toSubtask(eq(request), eq(parentTask), anyString());
    verify(repository).createTask(task);
  }

  // プロジェクト更新処理
  @Test
  void プロジェクト更新処理で適切なrepositoryとmapperが呼び出されていること() {
    ProjectRequest request = new ProjectRequest();
    Project project = new Project();

    when(mapper.toProject(request, null, PROJECT_PUBLIC_ID)).thenReturn(project);

    sut.updateProject(request, PROJECT_PUBLIC_ID);

    verify(mapper).toProject(request, null, PROJECT_PUBLIC_ID);
    verify(repository).updateProject(project);
    verify(repository).findProjectByProjectPublicId(PROJECT_PUBLIC_ID);
  }

  // タスク更新処理
  @Test
  void タスク更新処理で適切なrepositoryとmapperが呼び出されていること() {
    TaskRequest request = new TaskRequest();
    Task task = new Task();

    when(mapper.toTask(request, null, PROJECT_PUBLIC_ID)).thenReturn(task);

    sut.updateTask(request, PROJECT_PUBLIC_ID);

    verify(mapper).toTask(request, null, PROJECT_PUBLIC_ID);
    verify(repository).updateTask(task);
    verify(repository).findTaskByTaskPublicId(PROJECT_PUBLIC_ID);
  }

}
