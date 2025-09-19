package com.portfolio.taskapp.MyTaskManager.task.repository;

import static com.portfolio.taskapp.MyTaskManager.domain.enums.ProjectStatus.ACTIVE;
import static com.portfolio.taskapp.MyTaskManager.domain.enums.ProjectStatus.ARCHIVED;
import static org.assertj.core.api.Assertions.assertThat;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Project;
import com.portfolio.taskapp.MyTaskManager.domain.entity.Task;
import com.portfolio.taskapp.MyTaskManager.domain.enums.TaskPriority;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
          assertThat(project.getUserAccountId()).isEqualTo(userId);
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
    List<Task> actualSubtasks = actual.stream()
        .filter(task -> Objects.equals(task.getParentTaskId(), parentTaskId))
        .toList();

    // 検証
    assertThat(actual.size()).isEqualTo(2);
    assertThat(actualParent.size()).isEqualTo(1);
    assertThat(actualSubtasks.size()).isEqualTo(1);
    assertThat(actual)
        .allSatisfy(task -> assertThat(task.isDeleted()).isFalse());
  }

  @Test
  void projectPublicIdに紐づくプロジェクトの情報が取得できること() {
    String projectPublicId = "a1111111-bbbb-cccc-dddd-eeeeeeeeeeee";

    Project actual = sut.findProjectByProjectPublicId(projectPublicId);

    assertThat(actual).isNotNull();
    assertThat(actual.getPublicId()).isEqualTo(projectPublicId);
  }

  @Test
  void projectPublicIdに紐づくプロジェクトが論理削除済みの場合に情報が取得できないこと() {
    String projectPublicId = "a3333333-bbbb-cccc-dddd-eeeeeeeeeeee";

    Project actual = sut.findProjectByProjectPublicId(projectPublicId);

    assertThat(actual).isNull();
  }

  @Test
  void taskPublicIdに紐づく未削除タスクの情報が取得できること() {
    String taskPublicId = "22222222-bbbb-cccc-dddd-1234567890ab";

    Task actual = sut.findTaskByTaskPublicId(taskPublicId);

    assertThat(actual).isNotNull();
    assertThat(actual.getPublicId()).isEqualTo(taskPublicId);
  }

  @Test
  void taskPublicIdに紐づくタスクが削除済みの場合にタスクを取得できないこと() {
    String taskPublicId = "55555555-eeee-ffff-0000-1234567890ab";

    Task actual = sut.findTaskByTaskPublicId(taskPublicId);

    assertThat(actual).isNull();
  }

  @Test
  void プロジェクト登録処理で新規のプロジェクトが登録されDBでの内容も反映できていること() {
    String publicId = "00000000-0000-0000-0000-000000000000";
    Project project = Project.builder()
        .userAccountId(1)
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
        .userAccountId(1)
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
        .status(ARCHIVED)
        .build();

    sut.updateProject(project);

    Project actual = sut.findProjectByProjectPublicId(publicId);

    assertThat(actual)
        .usingRecursiveComparison()
        .comparingOnlyFields("projectCaption", "description", "status")
        .isEqualTo(project);
  }

  @Test
  void プロジェクトの更新で更新対象のレコードが論理削除済みの場合は更新されないこと() {
    String publicId = "a3333333-bbbb-cccc-dddd-eeeeeeeeeeee";
    Project project = Project.builder()
        .publicId(publicId)
        .projectCaption("更新プロジェクト名")
        .build();

    int actual = sut.updateProject(project);

    assertThat(actual).isEqualTo(0);
  }

  @Test
  void プロジェクトの更新対象でない項目は更新されないこと() {
    String publicId = "a1111111-bbbb-cccc-dddd-eeeeeeeeeeee";
    Project project = Project.builder()
        .id(999)
        .userAccountId(999)
        .publicId(publicId)
        .projectCaption("必須項目") // 必須制約のため入力（検証対象外）
        .createdAt(LocalDateTime
            .of(1900, 1, 1, 0, 0, 0))
        .isDeleted(true)
        .build();

    // 更新前情報の取得
    Project beforeProject = sut.findProjectByProjectPublicId(publicId);

    // 実行
    sut.updateProject(project);

    // 更新情報の取得
    Project actual = sut.findProjectByProjectPublicId(publicId);

    assertThat(actual)
        .usingRecursiveComparison()
        .comparingOnlyFields("id", "userId", "createdAt", "isDeleted")
        .isEqualTo(beforeProject);
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

  //タスク更新
  @Test
  void タスクの更新で更新対象のレコードが論理削除済みの場合は更新されないこと() {
    String publicId = "55555555-eeee-ffff-0000-1234567890ab";
    Task task = Task.builder()
        .publicId(publicId)
        .taskCaption("更新タスク名")
        .dueDate(LocalDate.of(2025, 9, 1))
        .build();

    int actual = sut.updateTask(task);

    assertThat(actual).isEqualTo(0);
  }

  @Test
  void タスクの更新処理で更新対象でない項目は更新されないこと() {
    String publicId = "11111111-aaaa-bbbb-cccc-1234567890ab";
    Task task = Task.builder()
        .id(999)
        .userAccountId(999)
        .projectId(999)
        .publicId(publicId)
        .parentTaskId(999)
        .taskCaption("必須項目")  // 必須制約のため入力（検証対象外）
        .dueDate(LocalDate.now())   // 必須制約のため入力（検証対象外）
        .createdAt(LocalDateTime.of(1900, 1, 1, 0, 0, 0))
        .isDeleted(true)
        .build();

    Task beforeTask = sut.findTaskByTaskPublicId(publicId);

    sut.updateTask(task);

    Task actual = sut.findTaskByTaskPublicId(publicId);

    assertThat(actual)
        .usingRecursiveComparison()
        .comparingOnlyFields("id", "userAccountId", "projectId", "parentTaskId", "createdAt",
            "isDeleted")
        .isEqualTo(beforeTask);
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
