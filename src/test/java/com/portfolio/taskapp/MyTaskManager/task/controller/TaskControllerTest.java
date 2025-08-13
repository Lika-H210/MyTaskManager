package com.portfolio.taskapp.MyTaskManager.task.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.portfolio.taskapp.MyTaskManager.task.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TaskController.class)
@ImportAutoConfiguration(exclude = SecurityAutoConfiguration.class)
class TaskControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private TaskService service;

  @Test
  void プロジェクトに紐づくタスク一覧取得時に適切なserviceが実行されていること() throws Exception {
    String projectPublicId = "00000000-0000-0000-0000-111111111111";

    mockMvc.perform(get("/projects/{projectPublicId}/tasks", projectPublicId))
        .andExpect(status().isOk());

    verify(service).getTasksByProjectPublicId(projectPublicId);
  }

  @Test
  void 親タスクに紐づく親子タスク取得時に適切なserviceが実行されていること() throws Exception {
    String taskPublicId = "00000000-0000-0000-0000-222222222222";

    mockMvc.perform(get("/tasks/{taskPublicId}", taskPublicId))
        .andExpect(status().isOk());

    verify(service).getTaskTreeByTaskPublicId(taskPublicId);
  }

}
