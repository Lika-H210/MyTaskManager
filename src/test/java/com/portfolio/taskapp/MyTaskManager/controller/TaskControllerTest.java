package com.portfolio.taskapp.MyTaskManager.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.portfolio.taskapp.MyTaskManager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
class TaskControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private TaskService service;

  @Test
  void 必要なserviceが実行されていること() throws Exception {
    String userPublicId = "00000000-0000-0000-0000-000000000000";

    mockMvc.perform(get("/my-project")
            .param("userPublicId", userPublicId))
        .andExpect(status().isOk());

    verify(service).getUserProjects(userPublicId);
  }

}