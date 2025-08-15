package com.portfolio.taskapp.MyTaskManager.task.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.taskapp.MyTaskManager.auth.config.SecurityConfig;
import com.portfolio.taskapp.MyTaskManager.auth.model.UserAccountDetails;
import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import com.portfolio.taskapp.MyTaskManager.domain.enums.ProjectStatus;
import com.portfolio.taskapp.MyTaskManager.domain.enums.TaskPriority;
import com.portfolio.taskapp.MyTaskManager.task.model.ProjectRequest;
import com.portfolio.taskapp.MyTaskManager.task.model.TaskRequest;
import com.portfolio.taskapp.MyTaskManager.task.service.TaskService;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TaskController.class)
@Import(SecurityConfig.class)
class TaskControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private TaskService service;

  private UserAccountDetails userDetails;

  private final String PROJECT_PUBLIC_ID = "00000000-0000-0000-0000-111111111111";
  private final String TASK_PUBLIC_ID = "00000000-0000-0000-0000-222222222222";

  @BeforeEach
  void setUpAuthentication() {
    UserAccount mockAccount = UserAccount.builder()
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
  void プロジェクトに紐づくタスク一覧取得時に適切なserviceが実行されていること() throws Exception {
    String projectPublicId = "00000000-0000-0000-0000-111111111111";

    mockMvc.perform(get("/projects/{projectPublicId}/tasks", projectPublicId)
            .with(user(userDetails)))
        .andExpect(status().isOk());

    verify(service).getTasksByProjectPublicId(projectPublicId);
  }

  @Test
  void 親タスクに紐づく親子タスク取得時に適切なserviceが実行されていること() throws Exception {
    String taskPublicId = "00000000-0000-0000-0000-222222222222";

    mockMvc.perform(get("/tasks/{taskPublicId}", taskPublicId)
            .with(user(userDetails)))
        .andExpect(status().isOk());

    verify(service).getTaskTreeByTaskPublicId(taskPublicId);
  }

  @Test
  void プロジェクト登録で201ステータスとなり適切にserviceが実行されていること() throws Exception {
    ProjectRequest project = createProjectRequestWithCaption("projectCaption");
    String json = objectMapper.writeValueAsString(project);

    mockMvc.perform(post("/projects")
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isCreated());

    verify(service).createProject(any(ProjectRequest.class),
        eq(userDetails.getAccount().getPublicId()));
  }

  @Test
  void プロジェクト登録でバリデーションに抵触する場合に400ステータスが返されること()
      throws Exception {
    ProjectRequest project = createProjectRequestWithCaption(null);
    String json = objectMapper.writeValueAsString(project);

    mockMvc.perform(post("/projects")
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest());

    verify(service, never()).createProject(any(), any());
  }

  @Test
  void 親タスク登録時に201ステータスとなり適切なServiceメソッドが呼び出されていること()
      throws Exception {
    String projectPublicId = "00000000-0000-0000-0000-111111111111";
    TaskRequest request = createTaskRequestWithCaption("taskCaption");

    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(post("/projects/{projectPublicId}/tasks", projectPublicId)
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isCreated());

    verify(service).createParentTask(any(TaskRequest.class), eq(projectPublicId));
  }

  @Test
  void 親タスク登録時でバリデーションに抵触する場合に400ステータスが返されること()
      throws Exception {
    String projectPublicId = "00000000-0000-0000-0000-111111111111";
    TaskRequest request = createTaskRequestWithCaption(null);

    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(post("/projects/{projectPublicId}/tasks", projectPublicId)
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest());

    verify(service, never()).createParentTask(any(), any());
  }

  @Test
  void 子タスク登録時に201ステータスとなり適切なServiceメソッドが呼び出されていること()
      throws Exception {
    String taskPublicId = "00000000-0000-0000-0000-222222222222";
    TaskRequest request = createTaskRequestWithCaption("caption");

    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(post("/tasks/{taskPublicId}/subtasks", taskPublicId)
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isCreated());

    verify(service).createSubtask(any(TaskRequest.class), eq(taskPublicId));
  }


  @Test
  void 子タスク登録時でバリデーションに抵触する場合にレスポンスで400エラーが返されること()
      throws Exception {
    String taskPublicId = "00000000-0000-0000-0000-222222222222";
    TaskRequest request = createTaskRequestWithCaption(null);

    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(post("/tasks/{taskPublicId}/subtasks", taskPublicId)
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest());

    verify(service, never()).createSubtask(any(), any());
  }

  @Test
  void プロジェクト更新処理で200ステータスになり適切なServiceが実行されること() throws Exception {
    String projectPublicId = "00000000-0000-0000-0000-111111111111";
    ProjectRequest request = createProjectRequestWithCaption("caption");

    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(put("/projects/{projectPublicId}", projectPublicId)
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isOk());

    verify(service).updateProject(any(ProjectRequest.class), eq(projectPublicId));
  }

  @Test
  void プロジェクト更新処理でバリデーションに抵触する場合にレスポンスで400エラーが返されること()
      throws Exception {
    String projectPublicId = "00000000-0000-0000-0000-111111111111";
    ProjectRequest request = createProjectRequestWithCaption(null);

    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(put("/projects/{projectPublicId}", projectPublicId)
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest());

    verify(service, never()).updateProject(any(), any());
  }

  @Test
  void タスク更新処理で200ステータスになり適切なServiceが実行されること() throws Exception {
    String taskPublicId = "00000000-0000-0000-0000-222222222222";
    TaskRequest request = createTaskRequestWithCaption("caption");

    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(put("/tasks/{taskPublicId}", taskPublicId)
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isOk());

    verify(service).updateTask(any(TaskRequest.class), eq(taskPublicId));
  }

  @Test
  void タスク更新処理でバリデーションに抵触する場合にレスポンスで400エラーが返されること()
      throws Exception {
    String taskPublicId = "00000000-0000-0000-0000-222222222222";
    TaskRequest request = createTaskRequestWithCaption(null);

    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(put("/tasks/{taskPublicId}", taskPublicId)
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest());

    verify(service, never()).updateTask(any(), any());
  }

  @Test
  void プロジェクト削除処理で204ステータスになり適切なserviceが実行されること()
      throws Exception {
    mockMvc.perform(delete("/projects/{projectPublicId}", PROJECT_PUBLIC_ID)
            .with(user(userDetails)))
        .andExpect(status().isNoContent());

    verify(service).deleteProject(PROJECT_PUBLIC_ID);
  }

  @Test
  void タスク削除処理で204ステータスになり適切なserviceが実行されること()
      throws Exception {
    mockMvc.perform(delete("/tasks/{taskPublicId}", TASK_PUBLIC_ID)
            .with(user(userDetails)))
        .andExpect(status().isNoContent());

    verify(service).deleteTask(TASK_PUBLIC_ID);
  }

  // ProjectRequest生成(Captionのみ引数で設定)
  private ProjectRequest createProjectRequestWithCaption(String caption) {
    return new ProjectRequest(caption, "description", ProjectStatus.ACTIVE);
  }

  // TaskRequest生成(Captionのみ引数で設定)
  private TaskRequest createTaskRequestWithCaption(String caption) {
    return new TaskRequest(
        caption,
        "description",
        LocalDate.now().plusDays(7),
        120,
        0,
        0,
        TaskPriority.LOW);
  }

}
