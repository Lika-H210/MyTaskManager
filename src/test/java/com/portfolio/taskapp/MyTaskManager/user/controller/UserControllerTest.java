package com.portfolio.taskapp.MyTaskManager.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.taskapp.MyTaskManager.user.model.AccountRegisterRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.AccountUpdateRequest;
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
    AccountRegisterRequest request = createAccountCreateRequest("user@email.com");

    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(post("/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isCreated())
        .andExpect(content().string("登録に成功しました。"));

    verify(service).registerUser(any(AccountRegisterRequest.class));
  }

  @Test
  void アカウント登録でバリデーションに抵触する場合にレスポンスで400エラーが返されること()
      throws Exception {
    AccountRegisterRequest request = createAccountCreateRequest(null);
    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(post("/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest());

    verify(service, never()).registerUser(any());
  }

  @Test
  void アカウント更新でバリデーションに抵触する場合にレスポンスで400エラーが返されること()
      throws Exception {
    AccountUpdateRequest request = new AccountUpdateRequest(null, "email", null, null);

    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(patch("/users/me")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest());

    verify(service, never()).updateAccount(any(), any());
  }

  // UserAccountCreateRequest生成(passwordのみ引数で設定)
  private static AccountRegisterRequest createAccountCreateRequest
  (String email) {
    return new AccountRegisterRequest(
        "user name",
        email,
        "password"
    );
  }

}