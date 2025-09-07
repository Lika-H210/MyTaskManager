package com.portfolio.taskapp.MyTaskManager.user.controller;

import com.portfolio.taskapp.MyTaskManager.auth.model.UserAccountDetails;
import com.portfolio.taskapp.MyTaskManager.exception.custom.InvalidPasswordChangeException;
import com.portfolio.taskapp.MyTaskManager.exception.custom.NotUniqueException;
import com.portfolio.taskapp.MyTaskManager.exception.custom.RecordNotFoundException;
import com.portfolio.taskapp.MyTaskManager.user.dto.AccountRegisterRequest;
import com.portfolio.taskapp.MyTaskManager.user.dto.AccountResponse;
import com.portfolio.taskapp.MyTaskManager.user.dto.update.AccountEmailUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.dto.update.AccountPasswordUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.dto.update.AccountUserInfoUpdateRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ユーザーアカウントに関する REST API を提供するコントローラクラス。
 * <p>
 * アカウント情報に対するCRUD処理を行います。<br> 基本的にレスポンスには AccountResponse を利用します。
 */
@RestController
@RequestMapping("/users")
@Validated
public class UserController {

  private final UserService service;

  @Autowired
  public UserController(UserService service) {
    this.service = service;
  }

  /**
   * ログイン中のユーザーアカウント情報を取得します。
   *
   * @param userDetails 現在認証済みのユーザー情報
   * @return ログイン中ユーザーのアカウント情報
   */
  @Operation(
      summary = "ユーザーアカウント情報の取得",
      description = "ユーザーアカウント情報を取得します。",
      security = @SecurityRequirement(name = "userAuth"),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "リクエストが正常に処理された場合",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = AccountResponse.class))
          )
      }
  )
  @GetMapping("/me")
  public ResponseEntity<AccountResponse> getMyAccount(
      @AuthenticationPrincipal UserAccountDetails userDetails) {
    AccountResponse response = service.findAccount(userDetails.getAccount().getPublicId());
    return ResponseEntity.ok(response);
  }

  /**
   * 新規ユーザーアカウントを登録します。
   *
   * @param request アカウント登録リクエスト
   * @return 登録成功メッセージ
   * @throws NotUniqueException 入力されたメールアドレスが既に使用されている場合
   */
  @Operation(
      summary = "ユーザーアカウントの登録",
      description = "新しいユーザーアカウントを登録します。登録後はログインが可能になります。",
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "アカウントの登録に成功した場合。メッセージが返されます。",
              content = @Content(schema = @Schema(type = "string", example = "アカウントを登録しました。ログインしてください。")
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "リクエストの内容が不正（入力値がバリデーション条件違反等）だった場合",
              content = @Content()
          )
      }
  )
  @PostMapping("/register")
  public ResponseEntity<String> registerAccount(
      @Valid @RequestBody AccountRegisterRequest request)
      throws NotUniqueException {
    service.registerUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body("登録に成功しました。");
  }

  /**
   * 認証情報以外のユーザー基本情報を更新します。
   *
   * @param userDetails 現在認証済みのユーザー情報
   * @param request     ユーザー情報の更新内容
   * @return 公開IDと更新後の基本情報を含むアカウント情報
   */
  @Operation(
      summary = "ユーザー情報の更新",
      description = "認証情報以外のユーザー情報を更新します。",
      security = @SecurityRequirement(name = "userAuth"),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "アカウント情報の更新に成功した場合",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = AccountResponse.class)
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "リクエストの内容が不正（入力値がバリデーション条件違反）だった場合",
              content = @Content()
          )
      }
  )
  @PutMapping("/me/info")
  public ResponseEntity<AccountResponse> updateUserInfo(
      @AuthenticationPrincipal UserAccountDetails userDetails,
      @Valid @RequestBody AccountUserInfoUpdateRequest request) {
    AccountResponse updateAccount = service.updateUserInfo(userDetails, request);
    return ResponseEntity.ok(updateAccount);
  }

  /**
   * ユーザーのメールアドレスを更新します。
   *
   * @param userDetails 現在認証済みのユーザー情報
   * @param request     メールアドレス更新リクエスト
   * @return 公開IDと更新後のメールアドレスを含むアカウント情報
   * @throws NotUniqueException 更新メールアドレスが既に他ユーザーに使用されている場合
   */
  @Operation(
      summary = "メルアドレスの更新",
      description = "アカウント情報（認証情報）のメルアドレスのみを更新します。",
      security = @SecurityRequirement(name = "userAuth"),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "更新に成功した場合",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = AccountResponse.class)
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "リクエストの内容が不正（入力値がバリデーション条件違反）だった場合",
              content = @Content()
          )
      }
  )
  @PutMapping("/me/email")
  public ResponseEntity<AccountResponse> updateEmail(
      @AuthenticationPrincipal UserAccountDetails userDetails,
      @Valid @RequestBody AccountEmailUpdateRequest request) throws NotUniqueException {
    AccountResponse updateAccount = service.updateEmail(userDetails, request);
    return ResponseEntity.ok(updateAccount);
  }

  /**
   * ユーザーのパスワードを更新します。
   *
   * @param userDetails 現在認証済みのユーザー情報
   * @param request     パスワードの更新リクエスト
   * @return 公開IDのみを含むアカウント情報。（更新されたパスワード情報は含みません）
   * @throws InvalidPasswordChangeException 現在のパスワードが一致しない場合
   */
  @Operation(
      summary = "パスワードの更新",
      description = "アカウント情報（認証情報）のパスワードのみを更新します。",
      security = @SecurityRequirement(name = "userAuth"),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "更新に成功した場合",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = AccountResponse.class)
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "リクエストの内容が不正（入力値がバリデーション条件違反）だった場合",
              content = @Content()
          )
      }
  )
  @PutMapping("/me/password")
  public ResponseEntity<AccountResponse> updatePassword(
      @AuthenticationPrincipal UserAccountDetails userDetails,
      @Valid @RequestBody AccountPasswordUpdateRequest request)
      throws InvalidPasswordChangeException {
    AccountResponse updateAccount = service.updatePassword(userDetails, request);
    return ResponseEntity.ok(updateAccount);
  }

  /**
   * ユーザーアカウントを削除します。
   *
   * @param userDetails 現在認証済みのユーザー情報
   * @param request     HTTP サーブレットリクエスト（ログアウト処理に使用）
   * @return 削除成功時はレスポンスボディなし
   * @throws RecordNotFoundException 指定されたユーザーが存在しない場合
   * @throws ServletException        ログアウト処理で例外が発生した場合
   */
  @Operation(
      summary = "ユーザーアカウントの削除",
      description = "アカウント情報からユーザーアカウントを論理削除します",
      security = @SecurityRequirement(name = "userAuth"),
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
    service.deleteAccount(userDetails.getAccount().getPublicId());
    request.logout();
    return ResponseEntity.noContent().build();
  }

}
