package com.portfolio.taskapp.MyTaskManager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Project;
import com.portfolio.taskapp.MyTaskManager.repository.TaskRepository;
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

  private TaskService sut;

  @BeforeEach
  void setUp() {
    sut = new TaskService(repository);
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
  void プロジェクトに紐づくタスク取得時に適切なrepositoryが呼び出せていること() {
    String projectPublicId = "00000000-0000-0000-0000-111111111111";
    Integer projectId = 9999;

    when(repository.findProjectIdByProjectPublicId(projectPublicId)).thenReturn(projectId);

    sut.getTasksByProjectPublicId(projectPublicId);

    verify(repository).findProjectIdByProjectPublicId(projectPublicId);
    verify(repository).findUserTasksByProjectId(projectId);
  }

}
