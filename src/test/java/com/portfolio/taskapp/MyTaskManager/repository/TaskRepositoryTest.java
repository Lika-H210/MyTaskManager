package com.portfolio.taskapp.MyTaskManager.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Project;
import com.portfolio.taskapp.MyTaskManager.domain.entity.Task;
import java.util.List;
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
  void プロジェクトのpublicIdと紐づくid情報が返されること() {
    Integer actual = sut.findProjectIdByProjectPublicId("a1111111-bbbb-cccc-dddd-eeeeeeeeeeee");

    assertThat(actual).isEqualTo(1);
  }

  @Test
  void ユーザーのIdに紐づくプロジェクトがすべて取得できていること() {
    Integer userId = 1;
    List<Project> actual = sut.findProjectsByUserId(userId);

    assertThat(actual.size()).isEqualTo(2);
    assertThat(actual)
        .allSatisfy(project -> assertThat(project.getUserId()).isEqualTo(userId));
  }

  @Test
  void プロジェクトのIdに紐づくタスクがすべて取得できていること() {
    Integer projectId = 1;
    List<Task> actual = sut.findTasksByProjectId(projectId);

    assertThat(actual.size()).isEqualTo(2);
    assertThat(actual)
        .allSatisfy(task -> assertThat(task.getProjectId()).isEqualTo(projectId));
  }

}
