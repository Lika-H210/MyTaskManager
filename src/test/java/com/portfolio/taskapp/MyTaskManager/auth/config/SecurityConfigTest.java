package com.portfolio.taskapp.MyTaskManager.auth.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.taskapp.MyTaskManager.user.controller.UserController;
import com.portfolio.taskapp.MyTaskManager.user.model.AccountRegisterRequest;
import com.portfolio.taskapp.MyTaskManager.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService service;

  @Test
  void permitAllのURLは未認証でもアクセス可能であること() throws Exception {
    AccountRegisterRequest request = new AccountRegisterRequest(
        "user name",
        "email@example.com",
        "password"
    );

    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(get("/login"))
        .andExpect(status().isOk());

    mockMvc.perform(post("/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isCreated());

  }

  @Test
  void anyRequestにあたるAPIは未認証としてリダイレクトされること() throws Exception {
    mockMvc.perform(get("/users/me"))
        .andExpect(status().isFound());
  }
}