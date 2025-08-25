package com.portfolio.taskapp.MyTaskManager.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.taskapp.MyTaskManager.auth.config.SecurityConfig;
import com.portfolio.taskapp.MyTaskManager.auth.model.UserAccountDetails;
import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import com.portfolio.taskapp.MyTaskManager.domain.enums.ProjectStatus;
import com.portfolio.taskapp.MyTaskManager.task.controller.TaskController;
import com.portfolio.taskapp.MyTaskManager.task.model.ProjectRequest;
import com.portfolio.taskapp.MyTaskManager.task.service.TaskService;
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
class TaskControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private TaskService service;

  private UserAccountDetails userDetails;

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
  void ユーザープロジェクトの一覧取得で認証情報がない場合302ステータスとなること()
      throws Exception {
    mockMvc.perform(get("/projects"))
        .andExpect(status().isFound());
  }

  @Test
  void プロジェクト登録で201ステータスとなり適切にserviceが実行されていること() throws Exception {
    ProjectRequest project = new ProjectRequest("projectCaption", "description",
        ProjectStatus.ACTIVE);
    String json = objectMapper.writeValueAsString(project);

    mockMvc.perform(post("/projects")
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isCreated());

    verify(service).createProject(any(ProjectRequest.class),
        eq(userDetails.getAccount().getPublicId()));
  }
}
