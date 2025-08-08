package com.portfolio.taskapp.MyTaskManager.task.repository;

import static com.portfolio.taskapp.MyTaskManager.domain.enums.ProjectStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Project;
import com.portfolio.taskapp.MyTaskManager.domain.entity.Task;
import com.portfolio.taskapp.MyTaskManager.domain.enums.ProjectStatus;
import com.portfolio.taskapp.MyTaskManager.domain.enums.TaskPriority;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;

@MybatisTest
class TaskRepositoryTest {

  @Autowired
  private TaskRepository sut;

  @Test
  void ユーザーのpublicIdに紐づくid情報が返されること() {
    Integer actual = sut.findUserIdByUserPublicId("5e8c0d2a-1234-4f99-a111-abcdef111111");

    assertThat(actual).isEqualTo(1);
  }

  @Test
  void 削除済みユーザーのpublicIdに紐づくid情報は返されないこと() {
    Integer actual = sut.findUserIdByUserPublicId("12345678-90ab-cdef-1234-abcdef123456");

    assertThat(actual).isNull();
  }

  @Test
  void プロジェクトのpublicIdと紐づくid情報が返されること() {
    Integer actual = sut.findProjectIdByProjectPublicId("a1111111-bbbb-cccc-dddd-eeeeeeeeeeee");

    assertThat(actual).isEqualTo(1);
  }

  @Test
  void 削除済みプロジェクトのpublicIdに紐づくid情報は返されないこと() {
    Integer actual = sut.findProjectIdByProjectPublicId("a3333333-bbbb-cccc-dddd-eeeeeeeeeeee");

    assertThat(actual).isNull();
  }

  @Test
  void タスクのpublicIdと紐づくid情報が返されること() {
    Integer actual = sut.findTaskIdByTaskPublicId("11111111-aaaa-bbbb-cccc-1234567890ab");

    assertThat(actual).isEqualTo(1);
  }

  @Test
  void 削除済みタスクのpublicIdに紐づくid情報は返されないこと() {
    Integer actual = sut.findTaskIdByTaskPublicId("55555555-eeee-ffff-0000-1234567890ab");

    assertThat(actual).isNull();
  }

  @Test
  void ユーザーのIdに紐づくプロジェクトのうち論理削除されていないプロジェクトのみ取得できていること() {
    Integer userId = 1;
    List<Project> actual = sut.findProjectsByUserId(userId);

    assertThat(actual.size()).isEqualTo(2);
    assertThat(actual)
        .allSatisfy(project -> {
          assertThat(project.getUserId()).isEqualTo(userId);
          assertThat(project.isDeleted()).isFalse();
        });
  }

  @Test
  void プロジェクトのIdに紐づくタスクのうち論理削除されていないタスクのみ取得できていること() {
    Integer projectId = 2;
    List<Task> actual = sut.findTasksByProjectId(projectId);

    assertThat(actual.size()).isEqualTo(2);
    assertThat(actual)
        .allSatisfy(task -> {
          assertThat(task.getProjectId()).isEqualTo(projectId);
          assertThat(task.isDeleted()).isFalse();
        });
  }

  @Test
  void 親タスクのIdに紐づく親子タスクのうち論理削除されていないタスクのみ取得できていること() {
    Integer parentTaskId = 3;

    List<Task> actual = sut.findTasksByTaskId(parentTaskId);

    // 検証前処理
    List<Task> actualParent = actual.stream()
        .filter(task -> task.getId().equals(parentTaskId))
        .toList();
    List<Task> actualChildTasks = actual.stream()
        .filter(task -> Objects.equals(task.getParentTaskId(), parentTaskId))
        .toList();

    // 検証
    assertThat(actual.size()).isEqualTo(2);
    assertThat(actualParent.size()).isEqualTo(1);
    assertThat(actualChildTasks.size()).isEqualTo(1);
    assertThat(actual)
        .allSatisfy(task -> assertThat(task.isDeleted()).isFalse());
  }

  @Test
  void プロジェクト登録処理で新規のプロジェクトが登録されDBでの内容も反映できていること() {
    String publicId = "00000000-0000-0000-0000-000000000000";
    Project project = Project.builder()
        .userId(1)
        .publicId(publicId)
        .projectCaption("テストプロジェクト")
        .description("説明")
        .status(ACTIVE)
        .build();

    sut.createProject(project);

    Project actual = sut.findProjectByProjectPublicId(publicId);

    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("createdAt", "updatedAt")
        .isEqualTo(project);
    assertThat(actual.getCreatedAt()).isNotNull();
    assertThat(actual.getUpdatedAt()).isNotNull();
  }

  @Test
  void タスクの登録処理で新規のタスクが登録されDB付与の内容も反映できていること() {
    String publicId = "00000000-0000-0000-0000-000000000000";
    Task task = Task.builder()
        .projectId(1)
        .publicId(publicId)
        .parentTaskId(1)
        .taskCaption("タスク名")
        .description("タスクの詳細説明")
        .dueDate(LocalDate.now().plusDays(1))
        .estimatedTime(120)
        .actualTime(60)
        .progress(50)
        .priority(TaskPriority.LOW)
        .build();

    sut.createTask(task);

    Task actual = sut.findTaskByTaskPublicId(publicId);

    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("createdAt", "updatedAt")
        .isEqualTo(task);
    assertThat(actual.getCreatedAt()).isNotNull();
    assertThat(actual.getUpdatedAt()).isNotNull();
  }

  @Test
  void プロジェクトの更新処理で必要な項目が更新されていること() {
    String publicId = "a1111111-bbbb-cccc-dddd-eeeeeeeeeeee";
    Project project = Project.builder()
        .publicId(publicId)
        .projectCaption("更新プロジェクト名")
        .description("更新プロジェクト詳細")
        .status(ProjectStatus.ARCHIVED)
        .build();

    sut.updateProject(project);

    Project actual = sut.findProjectByProjectPublicId(publicId);

    assertThat(actual)
        .usingRecursiveComparison()
        .comparingOnlyFields("projectCaption", "description", "status")
        .isEqualTo(project);
  }

  //タスク更新
  @Test
  void タスクの更新処理で更新対象である項目がすべて更新されていること() {
    String publicId = "11111111-aaaa-bbbb-cccc-1234567890ab";
    Task task = Task.builder()
        .publicId(publicId)
        .taskCaption("更新タスク名")
        .description("更新タスク詳細")
        .dueDate(LocalDate.of(2025, 9, 1))
        .estimatedTime(300)
        .actualTime(280)
        .progress(90)
        .priority(TaskPriority.MEDIUM)
        .build();

    sut.updateTask(task);

    Task actual = sut.findTaskByTaskPublicId(publicId);

    assertThat(actual)
        .usingRecursiveComparison()
        .comparingOnlyFields("taskCaption", "description", "dueDate", "estimatedTime",
            "actualTime", "progress", "priority")
        .isEqualTo(task);
  }

  @Test
  void プロジェクトの論理削除が行えていること() {
    String publicId = "a1111111-bbbb-cccc-dddd-eeeeeeeeeeee";

    sut.deleteProject(publicId);

    // 論理削除後は有効レコードが存在しないためactualにはnullが返る
    Integer actual = sut.findProjectIdByProjectPublicId(publicId);

    assertThat(actual).isNull();
  }

  @Test
  void タスクの論理削除が行えていること() {
    String publicId = "11111111-aaaa-bbbb-cccc-1234567890ab";

    sut.deleteTask(publicId);

    // 論理削除後は有効レコードが存在しないためactualにはnullが返る
    Integer actual = sut.findTaskIdByTaskPublicId(publicId);

    assertThat(actual).isNull();
  }

}
