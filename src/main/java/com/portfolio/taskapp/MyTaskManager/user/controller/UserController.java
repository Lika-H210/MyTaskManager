package com.portfolio.taskapp.MyTaskManager.user.controller;

import com.portfolio.taskapp.MyTaskManager.auth.model.UserAccountDetails;
import com.portfolio.taskapp.MyTaskManager.exception.NotUniqueException;
import com.portfolio.taskapp.MyTaskManager.user.model.ProfileUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.UserAccountCreateRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.UserAccountResponse;
import com.portfolio.taskapp.MyTaskManager.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

  private UserService service;

  @Autowired
  public UserController(UserService service) {
    this.service = service;
  }

  @Operation(
      summary = "ユーザーアカウント情報の取得",
      description = "ユーザーアカウント情報を取得します。",
      security = @SecurityRequirement(name = "basicAuth"),
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
  public ResponseEntity<String> registerUser(@Valid @RequestBody UserAccountCreateRequest request)
      throws NotUniqueException {
    service.registerUser(request);
    return ResponseEntity.ok("アカウントを登録しました。ログインしてください。");
  }

  @Operation(
      summary = "ユーザープロフィール情報の更新",
      description = "指定したユーザーのプロフィール情報（ユーザー名・メールアドレス）を更新します。パスワード更新は別APIで対応予定です。",
      security = @SecurityRequirement(name = "basicAuth"),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "プロフィール情報の更新に成功した場合、更新後のユーザー情報を返します。",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = UserAccountResponse.class)
              )
          )
      }
  )
  @PutMapping("/me/profile")
  public ResponseEntity<UserAccountResponse> updateUser(
      @AuthenticationPrincipal UserAccountDetails userDetails,
      @Valid @RequestBody ProfileUpdateRequest request) {
    UserAccountResponse response = service
        .updateProfile(userDetails.getAccount().getPublicId(), request);
    return ResponseEntity.ok(response);
  }

}
