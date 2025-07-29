package com.portfolio.taskapp.MyTaskManager.service.converter;

import static org.assertj.core.api.Assertions.assertThat;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Task;
import com.portfolio.taskapp.MyTaskManager.domain.model.TaskTree;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskConverterTest {

  private TaskConverter sut;

  @BeforeEach
  void setUp() {
    sut = new TaskConverter();
  }

  @Test
  void タスク一覧から親タスクと子タスクリストからなるTaskTreeのリストに変換できていること() {
    // 事前準備
    Task parentTask800 = Task.builder().id(800).parentTaskId(null).build();
    Task parentTask900 = Task.builder().id(900).parentTaskId(null).build();
    Task childTask801 = Task.builder().id(801).parentTaskId(800).build();
    Task childTask802 = Task.builder().id(802).parentTaskId(800).build();
    List<Task> taskList = List.of(parentTask800, childTask801, parentTask900, childTask802);

    // 実行
    List<TaskTree> actual = sut.convertToTaskTreeList(taskList);

    // 検証
    assertThat(actual).hasSize(2);

    assertThat(actual.getFirst().getParentTask()).isEqualTo(parentTask800);
    assertThat(actual.getFirst().getChildTaskList()).isEqualTo(List.of(childTask801, childTask802));

    assertThat(actual.get(1).getParentTask()).isEqualTo(parentTask900);
    assertThat(actual.get(1).getChildTaskList()).isEmpty();
  }

  @Test
  void 引数のタスクリストが子タスクのみの場合に空リストを返すこと() {
    Task childTask = Task.builder().id(801).parentTaskId(800).build();
    List<Task> taskList = List.of(childTask);

    List<TaskTree> actual = sut.convertToTaskTreeList(taskList);

    assertThat(actual).isEmpty();
  }

  @Test
  void 引数のタスク一覧が空の場合に空リストを返すこと() {
    List<TaskTree> actual = sut.convertToTaskTreeList(List.of());
    assertThat(actual).isEmpty();
  }

  @Test
  void 引数のタスク一覧がnullの場合に空リストを返すこと() {
    List<TaskTree> actual = sut.convertToTaskTreeList(null);
    assertThat(actual).isEmpty();
  }

}