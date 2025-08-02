package com.portfolio.taskapp.MyTaskManager.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Project;
import com.portfolio.taskapp.MyTaskManager.domain.entity.Task;
import com.portfolio.taskapp.MyTaskManager.domain.model.TaskTree;
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

  private TaskService sut;

  @BeforeEach
  void setUp() {
    sut = new TaskService(repository, converter);
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
}
