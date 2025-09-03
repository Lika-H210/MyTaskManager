package com.portfolio.taskapp.MyTaskManager.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.taskapp.MyTaskManager.user.dto.AccountRegisterRequest;
import com.portfolio.taskapp.MyTaskManager.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@ImportAutoConfiguration(exclude = SecurityAutoConfiguration.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService service;

  @Test
  void アカウント登録で適切なserviceが呼び出され201ステータスかつ成功メッセージが返り値となること()
      throws Exception {
    AccountRegisterRequest request = new AccountRegisterRequest(
        "user name",
        "user@email.com",
        "password"
    );

    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(post("/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isCreated())
        .andExpect(content().string("登録に成功しました。"));

    verify(service).registerUser(any(AccountRegisterRequest.class));
  }

}
