package com.portfolio.taskapp.MyTaskManager.task.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.taskapp.MyTaskManager.auth.config.SecurityConfig;
import com.portfolio.taskapp.MyTaskManager.auth.model.UserAccountDetails;
import com.portfolio.taskapp.MyTaskManager.domain.entity.Project;
import com.portfolio.taskapp.MyTaskManager.domain.entity.Task;
import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import com.portfolio.taskapp.MyTaskManager.domain.enums.ProjectStatus;
import com.portfolio.taskapp.MyTaskManager.domain.enums.TaskPriority;
import com.portfolio.taskapp.MyTaskManager.task.dto.ProjectRequest;
import com.portfolio.taskapp.MyTaskManager.task.dto.TaskRequest;
import com.portfolio.taskapp.MyTaskManager.task.service.TaskService;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TaskController.class)
@Import(SecurityConfig.class)
class TaskControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private TaskService service;

  private UserAccountDetails userDetails;

  private final Integer USER_ID = 999;
  private final Integer PROJECT_ID = 9999;
  private final String PROJECT_PUBLIC_ID = "00000000-0000-0000-0000-111111111111";
  private final String TASK_PUBLIC_ID = "00000000-0000-0000-0000-222222222222";

  @BeforeEach
  void setUpAuthentication() {
    UserAccount mockAccount = UserAccount.builder()
        .id(USER_ID)
        .publicId("00000000-0000-0000-0000-000000000000")
        .build();
    userDetails = new UserAccountDetails(mockAccount);
  }

  @Test
  void ユーザープロジェクトの一覧取得で適切にserviceが実行されていること() throws Exception {
    mockMvc.perform(get("/projects")
            .with(user(userDetails)))
        .andExpect(status().isOk());

    verify(service).getUserProjects(userDetails.getAccount().getPublicId());
  }

  @Test
  void 単独プロジェクト取得時に適切なserviceが実行されJsonで除外項目が含まれないレスポンスが返ること()
      throws Exception {
    Project project = Project.builder()
        .id(9999)
        .publicId(PROJECT_PUBLIC_ID)
        .userAccountId(USER_ID)
        .projectCaption("caption")
        .description("description")
        .status(ProjectStatus.ACTIVE)
        .build();

    when(service.getProjectByProjectPublicId(PROJECT_PUBLIC_ID, USER_ID)).thenReturn(project);
    String expectedJson = objectMapper.writeValueAsString(project);

    mockMvc.perform(get("/projects/{projectPublicId}", PROJECT_PUBLIC_ID)
            .with(user(userDetails)))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson))
        .andExpect(jsonPath("$.id").doesNotExist())
        .andExpect(jsonPath("$.userId").doesNotExist());

    verify(service).getProjectByProjectPublicId(PROJECT_PUBLIC_ID, USER_ID);
  }

  @Test
  void プロジェクトに紐づくタスク一覧取得時に適切なserviceが実行されること()
      throws Exception {
    mockMvc.perform(get("/projects/{projectPublicId}/task-trees", PROJECT_PUBLIC_ID)
            .with(user(userDetails)))
        .andExpect(status().isOk());

    verify(service).getTasksByProjectPublicId(PROJECT_PUBLIC_ID, USER_ID);
  }

  @Test
  void 親タスクに紐づく親子タスク取得時に適切なserviceが実行されていること()
      throws Exception {
    mockMvc.perform(get("/task-trees/{taskPublicId}", TASK_PUBLIC_ID)
            .with(user(userDetails)))
        .andExpect(status().isOk());

    verify(service).getTaskTreeByTaskPublicId(TASK_PUBLIC_ID, USER_ID);
  }

  @Test
  void 単独タスク取得時に適切なserviceが実行され親タスクのResponseに除外項目が含まれていないこと()
      throws Exception {
    Task task = Task.builder()
        .id(99999)
        .publicId(TASK_PUBLIC_ID)
        .userAccountId(USER_ID)
        .projectId(9999)
        .parentTaskId(90000)
        .build();

    when(service.getTaskByTaskPublicId(TASK_PUBLIC_ID, USER_ID)).thenReturn(task);

    mockMvc.perform(get("/tasks/{taskPublicId}", TASK_PUBLIC_ID)
            .with(user(userDetails)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").doesNotExist())
        .andExpect(jsonPath("$.userAccountId").doesNotExist())
        .andExpect(jsonPath("$.projectId").doesNotExist())
        .andExpect(jsonPath("$.parentTaskId").doesNotExist());

    verify(service).getTaskByTaskPublicId(TASK_PUBLIC_ID, USER_ID);
  }

  @Test
  void プロジェクト登録で201ステータスとなり適切にserviceが実行されていること() throws Exception {
    ProjectRequest project = new ProjectRequest("projectCaption", "description",
        ProjectStatus.ACTIVE);
    String json = objectMapper.writeValueAsString(project);

    mockMvc.perform(post("/projects")
            .with(user(userDetails))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isCreated());

    verify(service).createProject(any(ProjectRequest.class),
        eq(userDetails.getAccount().getPublicId()));
  }

  @Test
  void 親タスク登録時に201ステータスとなり適切なServiceメソッドが呼び出されていること()
      throws Exception {
    TaskRequest request = createNormalTaskRequest();

    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(post("/projects/{projectPublicId}/tasks", PROJECT_PUBLIC_ID)
            .with(user(userDetails))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isCreated());

    verify(service).createParentTask(any(TaskRequest.class), eq(PROJECT_PUBLIC_ID), eq(USER_ID));
  }

  @Test
  void 子タスク登録時に201ステータスとなり適切なServiceメソッドが呼び出されていること()
      throws Exception {
    TaskRequest request = createNormalTaskRequest();

    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(post("/tasks/{taskPublicId}/subtasks", TASK_PUBLIC_ID)
            .with(user(userDetails))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isCreated());

    verify(service).createSubtask(any(TaskRequest.class), eq(TASK_PUBLIC_ID), eq(USER_ID));
  }

  @Test
  void プロジェクト更新処理で200ステータスになり適切なserviceが実行されていること()
      throws Exception {
    ProjectRequest request = new ProjectRequest("caption", "description", ProjectStatus.ACTIVE);

    String requestJson = objectMapper.writeValueAsString(request);

    mockMvc.perform(put("/projects/{projectPublicId}", PROJECT_PUBLIC_ID)
            .with(user(userDetails))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isOk());

    verify(service).updateProject(any(ProjectRequest.class), eq(PROJECT_PUBLIC_ID), eq(USER_ID));
  }

  @Test
  void タスク更新処理で200ステータスになり適切なServiceが実行されること() throws Exception {
    TaskRequest request = createNormalTaskRequest();

    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(put("/tasks/{taskPublicId}", TASK_PUBLIC_ID)
            .with(user(userDetails))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isOk());

    verify(service).updateTask(any(TaskRequest.class), eq(TASK_PUBLIC_ID), eq(USER_ID));
  }

  @Test
  void プロジェクト削除処理で204ステータスになり適切なserviceが実行されること()
      throws Exception {
    mockMvc.perform(delete("/projects/{projectPublicId}", PROJECT_PUBLIC_ID)
            .with(user(userDetails))
            .with(csrf()))
        .andExpect(status().isNoContent());

    verify(service).deleteProject(PROJECT_PUBLIC_ID, USER_ID);
  }

  // 異常系：未認証での実行時挙動確認
  @Test
  void ユーザープロジェクトの一覧取得で認証情報がない場合302ステータスとなること()
      throws Exception {
    mockMvc.perform(get("/projects"))
        .andExpect(status().isFound());
  }

  // 異常系：400レスポンスの代表結合テスト
  @Test
  void プロジェクトリクエストでバリデーションに抵触する場合400ステータスとなり例外処理結果が返ること()
      throws Exception {
    ProjectRequest project = new ProjectRequest(null, "description", ProjectStatus.ACTIVE);
    String json = objectMapper.writeValueAsString(project);

    mockMvc.perform(post("/projects")
            .with(user(userDetails))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
        .andExpect(jsonPath("$.detail.projectCaption").isNotEmpty());

    verify(service, never()).createProject(any(), any());
  }

  @Test
  void タスクリクエストでバリデーションに抵触する場合400ステータスとなり例外処理結果が返ること()
      throws Exception {
    TaskRequest request = new TaskRequest(
        null,
        "description",
        LocalDate.now().plusDays(7),
        120,
        0,
        0,
        TaskPriority.LOW);

    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(
            post("/projects/{projectPublicId}/tasks", "00000000-0000-0000-0000-111111111111")
                .with(user(userDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
        .andExpect(jsonPath("$.detail.taskCaption").isNotEmpty());

    verify(service, never()).createParentTask(any(), any(), any());
  }

  @Test
  void タスク削除処理で204ステータスになり適切なserviceが実行されること()
      throws Exception {
    mockMvc.perform(delete("/tasks/{taskPublicId}", TASK_PUBLIC_ID)
            .with(user(userDetails))
            .with(csrf()))
        .andExpect(status().isNoContent());

    verify(service).deleteTask(TASK_PUBLIC_ID, USER_ID);
  }

  // TaskRequest生成(正常系)
  private TaskRequest createNormalTaskRequest() {
    return new TaskRequest(
        "caption",
        "description",
        LocalDate.now().plusDays(7),
        120,
        0,
        0,
        TaskPriority.LOW);
  }
}
