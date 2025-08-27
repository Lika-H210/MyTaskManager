package com.portfolio.taskapp.MyTaskManager.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.taskapp.MyTaskManager.auth.config.SecurityConfig;
import com.portfolio.taskapp.MyTaskManager.auth.model.UserAccountDetails;
import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import com.portfolio.taskapp.MyTaskManager.user.model.AccountResponse;
import com.portfolio.taskapp.MyTaskManager.user.model.AccountUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.service.UserService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService service;

  private UserAccountDetails userDetails;

  @BeforeEach
  void setUp() {
    UserAccount account = UserAccount.builder()
        .publicId("00000000-0000-0000-0000-000000000000")
        .build();
    userDetails = new UserAccountDetails(account);
  }

  @Test
  void アカウント情報取得時に適切なServiceが呼び出され200ステータスでJsonレスポンスが返されること()
      throws Exception {
    AccountResponse response = new AccountResponse(
        "00000000-0000-0000-0000-000000000000",
        "name",
        "email@example.com",
        LocalDateTime.now(),
        LocalDateTime.now());
    String expectJson = objectMapper.writeValueAsString(response);
    when(service.findAccount(userDetails.getAccount().getPublicId())).thenReturn(response);

    mockMvc.perform(get("/users/me")
            .with(user(userDetails)))
        .andExpect(status().isOk())
        .andExpect(content().json(expectJson));

    verify(service).findAccount(userDetails.getAccount().getPublicId());
  }

  @Test
  void アカウント更新で適切なserviceが呼び出され200ステータスが返されること()
      throws Exception {
    AccountUpdateRequest request = new AccountUpdateRequest(
        "New Name",
        "user@email.com",
        "oldPassword",
        "newPassword"
    );

    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(patch("/users/me")
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isOk());

    verify(service).updateAccount(any(UserAccountDetails.class), any(AccountUpdateRequest.class));
  }

  @Test
  void アカウント削除で適切なserviceが実行と認証情報の削除が実行され204ステータスが返されること()
      throws Exception {
    mockMvc.perform(delete("/users/me")
            .with(user(userDetails)))
        .andExpect(status().isNoContent())
        // 認証情報がクリアされることを確認
        .andExpect(unauthenticated());

    verify(service).deleteAccount(userDetails.getAccount().getPublicId());
  }

  // 異常系：未認証での実行時挙動確認
  @Test
  void アカウント情報取得時に認証情報がない場合は302ステータスが返されること()
      throws Exception {
    mockMvc.perform(get("/users/me"))
        .andExpect(status().isFound());
  }

  // 異常系：400レスポンスの代表結合テスト
  @Test
  void アカウント更新でリクエストオブジェクトがバリデーションに抵触した場合400レスポンスが返ること()
      throws Exception {
    AccountUpdateRequest request = new AccountUpdateRequest(null, "invalidEmail", null, null);

    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(patch("/users/me")
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
        .andExpect(jsonPath("$.detail.email").isArray())
        .andExpect(jsonPath("$.detail.email[0]").isNotEmpty());
    ;

    verify(service, never()).updateAccount(any(), any());
  }

}
