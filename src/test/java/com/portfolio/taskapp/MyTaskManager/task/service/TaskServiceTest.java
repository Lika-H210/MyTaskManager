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

  @BeforeEach
  void setUp() {
    sut = new TaskService(repository, converter, mapper);
  }

  @Test
  void ユーザープロジェクトの一覧取得で適切なrepositoryが呼び出せていること() {
    String userPublicId = "00000000-0000-0000-0000-000000000000";
    Integer userId = 999;

    when(repository.findUserIdByUserPublicId(userPublicId)).thenReturn(userId);

    sut.getUserProjects(userPublicId);

    verify(repository).findUserIdByUserPublicId(userPublicId);
    verify(repository).findProjectsByUserId(userId);
  }

  @Test
  void 未登録のユーザー公開IDでプロジェクト一覧取得を実行すた場合に早期リターンで空のリストが返されること() {
    String userPublicId = "00000000-0000-0000-0000-000000000000";

    when(repository.findUserIdByUserPublicId(userPublicId)).thenReturn(null);

    List<Project> actual = sut.getUserProjects(userPublicId);

    verify(repository, never()).findProjectsByUserId(any());
    assertThat(actual).isEmpty();
  }

  @Test
  void プロジェクトに紐づくタスク取得時に適切なrepositoryとconverterが呼び出せていること() {
    String projectPublicId = "00000000-0000-0000-0000-111111111111";
    Integer projectId = 9999;
    List<Task> taskList = List.of();

    when(repository.findProjectIdByProjectPublicId(projectPublicId)).thenReturn(projectId);
    when(repository.findTasksByProjectId(projectId)).thenReturn(taskList);

    sut.getTasksByProjectPublicId(projectPublicId);

    verify(repository).findProjectIdByProjectPublicId(projectPublicId);
    verify(repository).findTasksByProjectId(projectId);
    verify(converter).convertToTaskTreeList(taskList);
  }

  // 正常系
  @Test
  void 単独の親子タスク取得する際に必要なrepositoryとconverterが呼び出されていること() {
    // 事前準備
    String taskPublicId = "00000000-0000-0000-0000-222222222222";
    Integer taskId = 99999;
    List<Task> taskList = List.of();
    TaskTree taskTree = new TaskTree();
    List<TaskTree> taskTreesList = List.of(taskTree);

    when(repository.findTaskIdByTaskPublicId(taskPublicId)).thenReturn(taskId);
    when(repository.findTasksByTaskId(taskId)).thenReturn(taskList);
    when(converter.convertToTaskTreeList(taskList)).thenReturn(taskTreesList);

    // 実行
    TaskTree actual = sut.getTaskTreeByTaskPublicId(taskPublicId);

    // 検証
    verify(repository).findTaskIdByTaskPublicId(taskPublicId);
    verify(repository).findTasksByTaskId(taskId);
    verify(converter).convertToTaskTreeList(taskList);

    assertThat(actual).isEqualTo(taskTree);
  }

  // 異常系；convert結果が空の場合(repositoryのスタブは正常系でテスト済みのため省略)
  @Test
  void 単独の親子タスク取得する際にconvert処理で空のリストが返った場合に例外処理が実行されること() {
    // 事前準備
    String taskPublicId = "00000000-0000-0000-0000-222222222222";

    when(converter.convertToTaskTreeList(anyList())).thenReturn(List.of());

    assertThatThrownBy(() -> sut.getTaskTreeByTaskPublicId(taskPublicId))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("指定されたタスクに対応するTaskTreeが1件ではありません");
  }

  // 異常系；convert結果が複数要素のリストの場合(repositoryのスタブは正常系でテスト済みのため省略)
  @Test
  void 単独の親子タスク取得する際にconvert処理で複数要素のリストが返った場合に例外処理が実行されること() {
    // 事前準備
    String taskPublicId = "00000000-0000-0000-0000-222222222222";
    List<TaskTree> taskTreesList = List.of(new TaskTree(), new TaskTree());

    when(converter.convertToTaskTreeList(anyList())).thenReturn(taskTreesList);

    assertThatThrownBy(() -> sut.getTaskTreeByTaskPublicId(taskPublicId))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("指定されたタスクに対応するTaskTreeが1件ではありません");
  }

  // プロジェクト登録処理
  @Test
  void プロジェクト登録処理で適切なrepositoryとmapperが呼び出されていること() {
    String userPublicId = "00000000-0000-0000-000000000000";
    Integer userId = 999;
    ProjectRequest request = new ProjectRequest();
    Project project = new Project();

    when(repository.findUserIdByUserPublicId(userPublicId)).thenReturn(userId);
    when(mapper.toProject(eq(request), eq(userId), any(String.class))).thenReturn(project);

    sut.createProject(request, userPublicId);

    verify(repository).findUserIdByUserPublicId(userPublicId);
    verify(mapper).toProject(eq(request), eq(userId), any(String.class));
    verify(repository).createProject(project);
  }

  @Test
  void 親タスク登録処理で適切なrepositoryとmapperが呼び出されていること() {
    String projectPublicId = "00000000-0000-0000-000000000000";
    Integer projectId = 999;
    TaskRequest request = new TaskRequest();
    Task task = new Task();

    when(repository.findProjectIdByProjectPublicId(projectPublicId)).thenReturn(projectId);
    when(mapper.toBaseTask(eq(request), eq(projectId), any(String.class))).thenReturn(task);

    sut.createParentTask(request, projectPublicId);

    verify(repository).findProjectIdByProjectPublicId(projectPublicId);
    verify(mapper).toBaseTask(eq(request), eq(projectId), any(String.class));
    verify(repository).createTask(task);
  }

  @Test
  void 子タスク登録処理で適切なrepositoryとmapperが呼び出されていること() {
    String taskPublicId = "00000000-0000-0000-0000-222222222222";
    Integer taskId = 999;
    Integer projectId = 111;
    TaskRequest request = new TaskRequest();
    Task task = new Task();

    when(repository.findTaskIdByTaskPublicId(taskPublicId)).thenReturn(taskId);
    when(repository.findProjectIdByTaskId(taskId)).thenReturn(projectId);
    when(mapper.toSubtask(eq(request), eq(projectId), anyString(), eq(taskId))).thenReturn(
        task);

    sut.createSubtask(request, taskPublicId);

    verify(repository).findTaskIdByTaskPublicId(taskPublicId);
    verify(repository).findProjectIdByTaskId(taskId);
    verify(mapper).toSubtask(eq(request), eq(projectId), anyString(), eq(taskId));
    verify(repository).createTask(task);
  }

  // プロジェクト更新処理
  @Test
  void プロジェクト更新処理で適切なrepositoryとmapperが呼び出されていること() {
    String projectPublicId = "00000000-0000-0000-111111111111";
    ProjectRequest request = new ProjectRequest();
    Project project = new Project();

    when(mapper.toProject(request, null, projectPublicId)).thenReturn(project);

    sut.updateProject(request, projectPublicId);

    verify(mapper).toProject(request, null, projectPublicId);
    verify(repository).updateProject(project);
    verify(repository).findProjectByProjectPublicId(projectPublicId);
  }

  // タスク更新処理
  @Test
  void タスク更新処理で適切なrepositoryとmapperが呼び出されていること() {
    String taskPublicId = "00000000-0000-0000-222222222222";
    TaskRequest request = new TaskRequest();
    Task task = new Task();

    when(mapper.toBaseTask(request, null, taskPublicId)).thenReturn(task);

    sut.updateTask(request, taskPublicId);

    verify(mapper).toBaseTask(request, null, taskPublicId);
    verify(repository).updateTask(task);
    verify(repository).findTaskByTaskPublicId(taskPublicId);
  }

}
