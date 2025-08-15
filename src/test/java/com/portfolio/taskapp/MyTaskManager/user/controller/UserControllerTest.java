package com.portfolio.taskapp.MyTaskManager.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.taskapp.MyTaskManager.auth.config.SecurityConfig;
import com.portfolio.taskapp.MyTaskManager.auth.model.UserAccountDetails;
import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import com.portfolio.taskapp.MyTaskManager.user.model.AccountUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.UserAccountCreateRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.UserAccountResponse;
import com.portfolio.taskapp.MyTaskManager.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService service;

  @MockitoBean
  private HttpServletRequest servletRequest;

  private UserAccountDetails userDetails;

  @BeforeEach
  void setUp() {
    UserAccount account = UserAccount.builder()
        .publicId("00000000-0000-0000-0000-000000000000")
        .build();
    userDetails = new UserAccountDetails(account);
  }

  // レスポンスのJSON形式検証は本メソッドのみで行っています。
  @Test
  void アカウント情報取得時に適切なServiceが呼び出され200ステータスとJSON形式のレスポンスが返されること()
      throws Exception {
    UserAccountResponse response = new UserAccountResponse();

    when(service.findAccount(userDetails.getAccount().getPublicId())).thenReturn(response);

    String expectedJson = objectMapper.writeValueAsString(response);

    mockMvc.perform(get("/users/me")
            .with(user(userDetails)))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));

    verify(service).findAccount(userDetails.getAccount().getPublicId());
  }

  @Test
  void アカウント登録で適切なserviceが呼び出され201ステータスかつ成功メッセージが返り値となること()
      throws Exception {
    UserAccountCreateRequest request = createAccountCreateRequest("user@email.com");

    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(post("/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isCreated())
        .andExpect(content().string("登録に成功しました。"));

    verify(service).registerUser(any(UserAccountCreateRequest.class));
  }

  @Test
  void アカウント登録でバリデーションに抵触する場合にレスポンスで400エラーが返されること()
      throws Exception {
    UserAccountCreateRequest request = createAccountCreateRequest(null);
    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(post("/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest());

    verify(service, never()).registerUser(any());
  }

  @Test
  void アカウント更新で更新情報がある場合に適切なserviceが呼び出され200ステータスが返されること()
      throws Exception {
    AccountUpdateRequest request = createAccountUpdateRequest("user@email.com");

    when(service.updateAccount(any(UserAccountDetails.class), any(AccountUpdateRequest.class)))
        .thenReturn(new UserAccountResponse());

    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(patch("/users/me")
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isOk());

    verify(service).updateAccount(any(UserAccountDetails.class), any(AccountUpdateRequest.class));
  }

  @Test
  void アカウント更新で更新情報がない場合に適切なserviceが呼び出され204ステータスが返されること()
      throws Exception {
    AccountUpdateRequest request = new AccountUpdateRequest();

    when(service.updateAccount(any(UserAccountDetails.class), any(AccountUpdateRequest.class)))
        .thenReturn(null);

    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(patch("/users/me")
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isNoContent());

    verify(service).updateAccount(any(UserAccountDetails.class), any(AccountUpdateRequest.class));
  }

  @Test
  void アカウント更新でバリデーションに抵触する場合にレスポンスで400エラーが返されること()
      throws Exception {
    AccountUpdateRequest request = createAccountUpdateRequest("email");
    String json = objectMapper.writeValueAsString(request);

    mockMvc.perform(patch("/users/me")
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest());

    verify(service, never()).updateAccount(any(), any());
  }

  @Test
  void アカウント削除で適切なserviceとログアウト処理が呼び出され204ステータスが返されること()
      throws Exception {
    mockMvc.perform(delete("/users/me")
            .with(user(userDetails)))
        .andExpect(status().isNoContent())
        .andExpect(request().sessionAttributeDoesNotExist("SPRING_SECURITY_CONTEXT"));
    ;
    verify(service).deleteAccount(userDetails.getAccount().getPublicId());
  }

  // UserAccountCreateRequest生成(passwordのみ引数で設定)
  private static UserAccountCreateRequest createAccountCreateRequest
  (String email) {
    return new UserAccountCreateRequest(
        "user name",
        email,
        "password"
    );
  }

  private static AccountUpdateRequest createAccountUpdateRequest
      (String email) {
    return new AccountUpdateRequest(
        "New Name",
        email,
        "oldPassword",
        "newPassword"
    );
  }
}