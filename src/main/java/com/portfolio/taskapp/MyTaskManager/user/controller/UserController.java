package com.portfolio.taskapp.MyTaskManager.user.controller;

import com.portfolio.taskapp.MyTaskManager.auth.model.UserAccountDetails;
import com.portfolio.taskapp.MyTaskManager.exception.InvalidPasswordChangeException;
import com.portfolio.taskapp.MyTaskManager.exception.NotUniqueException;
import com.portfolio.taskapp.MyTaskManager.user.model.AccountUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.UserAccountCreateRequest;
import com.portfolio.taskapp.MyTaskManager.user.model.UserAccountResponse;
import com.portfolio.taskapp.MyTaskManager.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
          ),
          @ApiResponse(
              responseCode = "400",
              description = "リクエストの内容が不正（入力値がバリデーション条件違反）だった場合",
              content = @Content()
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
      summary = "ユーザー情報の更新",
      description = "ユーザー情報を更新します。",
      security = @SecurityRequirement(name = "basicAuth"),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "アカウント情報の更新に成功した場合",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = UserAccountResponse.class)
              )
          ),
          @ApiResponse(
              responseCode = "204",
              description = "更新内容がない場合",
              content = @Content()
          ),
          @ApiResponse(
              responseCode = "400",
              description = "リクエストの内容が不正（入力値がバリデーション条件違反）だった場合",
              content = @Content()
          ),
          @ApiResponse(
              responseCode = "401",
              description = "パスワードの検証に失敗した場合",
              content = @Content()
          )
      }
  )
  @PatchMapping("/me/account")
  public ResponseEntity<UserAccountResponse> updateAccount(
      @AuthenticationPrincipal UserAccountDetails userDetails,
      @Valid @RequestBody AccountUpdateRequest request)
      throws NotUniqueException, InvalidPasswordChangeException {
    UserAccountResponse updateAccount = service.updateAccount(userDetails, request);
    if (updateAccount == null) {
      // 何も更新がなかった場合
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(updateAccount);
  }

  @Operation(
      summary = "ユーザーアカウントの削除",
      description = "アカウント情報からユーザーアカウントを論理削除します",
      security = @SecurityRequirement(name = "basicAuth"),
      responses = {
          @ApiResponse(
              responseCode = "204",
              description = "削除が成功した場合（レスポンスボディはありません）"
                  + "この操作により現在のセッションが終了し、再度アクセスするには再ログインが必要になります。"
          ),
          @ApiResponse(
              responseCode = "404",
              description = "ログインアカウントのユーザー情報が存在しないか、削除されている場合"
          )
      }
  )
  @DeleteMapping("/me")
  public ResponseEntity<Void> deleteAccount(
      @AuthenticationPrincipal UserAccountDetails userDetails,
      HttpServletRequest request) throws ServletException {
    request.logout();
    service.deleteAccount(userDetails.getAccount().getPublicId());
    return ResponseEntity.noContent().build();
  }

}
