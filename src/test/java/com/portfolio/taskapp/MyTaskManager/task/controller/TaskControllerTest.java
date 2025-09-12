package com.portfolio.taskapp.MyTaskManager.task.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.taskapp.MyTaskManager.domain.entity.Project;
import com.portfolio.taskapp.MyTaskManager.domain.enums.ProjectStatus;
import com.portfolio.taskapp.MyTaskManager.domain.enums.TaskPriority;
import com.portfolio.taskapp.MyTaskManager.task.dto.ProjectRequest;
import com.portfolio.taskapp.MyTaskManager.task.dto.TaskRequest;
import com.portfolio.taskapp.MyTaskManager.task.service.TaskService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TaskController.class)
@ImportAutoConfiguration(exclude = SecurityAutoConfiguration.class)
class TaskControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private TaskService service;

  private final String PROJECT_PUBLIC_ID = "00000000-0000-0000-0000-111111111111";
  private final String TASK_PUBLIC_ID = "00000000-0000-0000-0000-222222222222";

  @Test
  void 親タスク登録時に201ステータスとなり適切なServiceメソッドが呼び出されていること()
      throws Exception {
    TaskRequest request = createNormalTaskRequest();

    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(post("/projects/{projectPublicId}/tasks", PROJECT_PUBLIC_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isCreated());

    verify(service).createParentTask(any(TaskRequest.class), eq(PROJECT_PUBLIC_ID));
  }

  @Test
  void 子タスク登録時に201ステータスとなり適切なServiceメソッドが呼び出されていること()
      throws Exception {
    TaskRequest request = createNormalTaskRequest();

    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(post("/tasks/{taskPublicId}/subtasks", TASK_PUBLIC_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isCreated());

    verify(service).createSubtask(any(TaskRequest.class), eq(TASK_PUBLIC_ID));
  }

  @Test
  void プロジェクト更新処理で200ステータスになり適切なserviceが実行されていること()
      throws Exception {
    ProjectRequest request = new ProjectRequest("caption", "description", ProjectStatus.ACTIVE);
    Project response = Project.builder()
        .id(9999)
        .userId(999)
        .build();

    String requestJson = objectMapper.writeValueAsString(request);

    when(service.updateProject(any(ProjectRequest.class), eq(PROJECT_PUBLIC_ID)))
        .thenReturn(response);

    mockMvc.perform(put("/projects/{projectPublicId}", PROJECT_PUBLIC_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isOk());

    verify(service).updateProject(any(ProjectRequest.class), eq(PROJECT_PUBLIC_ID));
  }

  @Test
  void タスク更新処理で200ステータスになり適切なServiceが実行されること() throws Exception {
    TaskRequest request = createNormalTaskRequest();

    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(put("/tasks/{taskPublicId}", TASK_PUBLIC_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isOk());

    verify(service).updateTask(any(TaskRequest.class), eq(TASK_PUBLIC_ID));
  }

  @Test
  void プロジェクト削除処理で204ステータスになり適切なserviceが実行されること()
      throws Exception {
    mockMvc.perform(delete("/projects/{projectPublicId}", PROJECT_PUBLIC_ID))
        .andExpect(status().isNoContent());

    verify(service).deleteProject(PROJECT_PUBLIC_ID);
  }

  @Test
  void タスク削除処理で204ステータスになり適切なserviceが実行されること()
      throws Exception {
    mockMvc.perform(delete("/tasks/{taskPublicId}", TASK_PUBLIC_ID))
        .andExpect(status().isNoContent());

    verify(service).deleteTask(TASK_PUBLIC_ID);
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
