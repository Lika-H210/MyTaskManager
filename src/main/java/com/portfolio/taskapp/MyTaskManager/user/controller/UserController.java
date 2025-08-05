package com.portfolio.taskapp.MyTaskManager.user.controller;

import com.portfolio.taskapp.MyTaskManager.auth.model.UserAccountDetails;
import com.portfolio.taskapp.MyTaskManager.user.model.UserAccountCreateRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.UserAccountResponse;
import com.portfolio.taskapp.MyTaskManager.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  private UserService service;

  @Autowired
  public UserController(UserService service) {
    this.service = service;
  }

  @Operation(
      summary = "ユーザーアカウント情報の取得",
      description = "ユーザーアカウント情報を取得します。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "リクエストが正常に処理された場合",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = UserAccountResponse.class))
          )
      }
  )
  @GetMapping("/me")
  public ResponseEntity<UserAccountResponse> getMyAccountInfo(
      @AuthenticationPrincipal UserAccountDetails userDetails) {
    UserAccountResponse response = service.findAccount(userDetails.getAccount().getPublicId());
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "ユーザーアカウントの登録",
      description = "新しいユーザーアカウントを登録します。登録後はログインが可能になります。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "アカウントの登録に成功した場合。メッセージが返されます。",
              content = @Content(schema = @Schema(type = "string", example = "アカウントを登録しました。ログインしてください。")
              )
          )
      }
  )
  @PostMapping("/register")
  public ResponseEntity<String> registerUser(@Valid @RequestBody UserAccountCreateRequest request) {
    service.registerUser(request);
    return ResponseEntity.ok("アカウントを登録しました。ログインしてください。");
  }

}
