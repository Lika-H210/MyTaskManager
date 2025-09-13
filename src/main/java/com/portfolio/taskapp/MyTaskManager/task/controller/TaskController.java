package com.portfolio.taskapp.MyTaskManager.task.controller;

import com.portfolio.taskapp.MyTaskManager.auth.model.UserAccountDetails;
import com.portfolio.taskapp.MyTaskManager.domain.entity.Project;
import com.portfolio.taskapp.MyTaskManager.domain.entity.Task;
import com.portfolio.taskapp.MyTaskManager.exception.custom.RecordNotFoundException;
import com.portfolio.taskapp.MyTaskManager.task.dto.ProjectRequest;
import com.portfolio.taskapp.MyTaskManager.task.dto.TaskRequest;
import com.portfolio.taskapp.MyTaskManager.task.dto.TaskTree;
import com.portfolio.taskapp.MyTaskManager.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * プロジェクトおよびタスクに関する REST API を提供するコントローラクラス。
 * <p>
 * 認証済みユーザーのプロジェクト・タスクに対するCRUD処理を行います。
 */
@RestController
@Validated
public class TaskController {

  private final TaskService service;

  @Autowired
  public TaskController(TaskService service) {
    this.service = service;
  }

  /**
   * 認証済みユーザーに紐づくプロジェクト一覧を取得します。
   *
   * @param userDetails 現在認証済みのユーザー情報
   * @return ログイン中ユーザーに紐づくプロジェクトのリスト
   */
  @Operation(
      summary = "ユーザープロジェクトの一覧取得",
      description = "認証されたユーザーに紐づくプロジェクト情報の一覧を取得します",
      security = @SecurityRequirement(name = "userAuth"),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "リクエストが正常に処理された場合",
              content = @Content(mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = Project.class)))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "認証済みユーザーが削除済みまたは無効化されている場合",
              content = @Content()
          )
      }
  )
  @GetMapping("/projects")
  public List<Project> getProjectList(@AuthenticationPrincipal UserAccountDetails userDetails) {
    return service.getUserProjects(userDetails.getAccount().getPublicId());
  }

  /**
   * 指定した公開IDに紐づくプロジェクトを取得します。
   *
   * @param userDetails     現在認証済みのユーザー情報
   * @param projectPublicId プロジェクトの公開ID
   * @return プロジェクト情報
   * @throws RecordNotFoundException プロジェクトが存在しない場合
   */
  @Operation(
      summary = "プロジェクトの単体取得",
      description = "プロジェクトの公開Idに紐づくプロジェクト情報を取得します",
      security = @SecurityRequirement(name = "userAuth"),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "リクエストが正常に処理された場合",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = Project.class))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "指定した公開IDのプロジェクトが存在しないか、削除されている場合",
              content = @Content()
          )
      }
  )
  @GetMapping("/projects/{projectPublicId}")
  public ResponseEntity<Project> getProject(
      @AuthenticationPrincipal UserAccountDetails userDetails,
      @PathVariable
      @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
          message = "入力の形式に誤りがあります")
      String projectPublicId) {
    Project project = service.getProjectByProjectPublicId(projectPublicId,
        userDetails.getAccount().getId());
    return ResponseEntity.ok(project);
  }

  /**
   * 指定したプロジェクトに紐づくタスクを、親子関係の階層構造を単位とする一覧として取得します。
   *
   * @param userDetails     現在認証済みのユーザー情報
   * @param projectPublicId プロジェクトの公開ID
   * @return 親子タスクのリスト
   * @throws RecordNotFoundException プロジェクトが存在しない場合
   */
  @Operation(
      summary = "プロジェクトの親子タスク一覧取得",
      description = "プロジェクトに紐づくタスクを、親子関係の階層構造を単位とする一覧として取得します。",
      security = @SecurityRequirement(name = "userAuth"),
      parameters = {
          @Parameter(
              name = "projectPublicId",
              required = true,
              description = "プロジェクトの公開ID（UUID）",
              schema = @Schema(type = "string", format = "uuid",
                  example = "5998fd5d-a2cd-11ef-b71f-6845f15f510c")
          )
      },
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "リクエストが正常に処理された場合",
              content = @Content(mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = TaskTree.class)))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "指定した公開IDのプロジェクトが存在しないか、削除されている場合",
              content = @Content()
          )
      }
  )
  @GetMapping("/projects/{projectPublicId}/task-trees")
  public List<TaskTree> getTaskTreeList(
      @AuthenticationPrincipal UserAccountDetails userDetails,
      @PathVariable
      @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
          message = "入力の形式に誤りがあります")
      String projectPublicId) {
    return service.getTasksByProjectPublicId(projectPublicId, userDetails.getAccount().getId());
  }

  /**
   * 指定した親タスクの公開IDに紐づくタスクを、親子関係の階層構造で取得します。
   *
   * @param userDetails  現在認証済みのユーザー情報
   * @param taskPublicId 親タスクの公開ID（UUID形式）
   * @return 親子タスク
   * @throws RecordNotFoundException タスクが存在しない場合
   */
  @Operation(
      summary = "単独の親子タスク取得",
      description = "親タスクの公開IDに紐づくタスクを、親子関係の階層構造で取得します。",
      security = @SecurityRequirement(name = "userAuth"),
      parameters = {
          @Parameter(
              name = "taskPublicId",
              required = true,
              description = "親タスクの公開ID（UUID）",
              schema = @Schema(type = "string", format = "uuid",
                  example = "5998fd5d-a2cd-11ef-b71f-6845f15f510c")
          )
      },
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "リクエストが正常に処理された場合",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = TaskTree.class))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "指定した公開IDのタスクが存在しないか、削除されている場合",
              content = @Content()
          )
      }
  )
  @GetMapping("/task-trees/{taskPublicId}")
  public TaskTree getTaskTree(
      @AuthenticationPrincipal UserAccountDetails userDetails,
      @PathVariable
      @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
          message = "入力の形式に誤りがあります")
      String taskPublicId) {
    return service.getTaskTreeByTaskPublicId(taskPublicId, userDetails.getAccount().getId());
  }

  /**
   * 指定した公開IDに紐づく単体タスクを取得します。
   *
   * @param userDetails  現在認証済みのユーザー情報
   * @param taskPublicId タスクの公開ID（UUID形式）
   * @return タスク情報
   * @throws RecordNotFoundException タスクが存在しない場合
   */
  @Operation(
      summary = "タスクの単体取得",
      description = "タスクの公開Idに紐づくタスク情報を取得します",
      security = @SecurityRequirement(name = "userAuth"),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "リクエストが正常に処理された場合",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = Task.class))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "指定した公開IDのタスクが存在しないか、削除されている場合",
              content = @Content()
          )
      }
  )
  @GetMapping("/tasks/{taskPublicId}")
  public ResponseEntity<Task> getTask(
      @AuthenticationPrincipal UserAccountDetails userDetails,
      @PathVariable
      @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
          message = "入力の形式に誤りがあります")
      String taskPublicId) {
    Task task = service.getTaskByTaskPublicId(taskPublicId, userDetails.getAccount().getId());
    return ResponseEntity.ok(task);
  }

  /**
   * 認証済みユーザーの新規プロジェクトを登録します。
   *
   * @param userDetails 現在認証済みのユーザー情報
   * @param request     プロジェクト登録リクエスト
   * @return 作成されたプロジェクト情報
   * @throws RecordNotFoundException ユーザーが存在しない場合
   */
  @Operation(
      summary = "新規プロジェクト登録",
      description = "認証されたユーザーに紐づく新規のプロジェクトを登録します。",
      security = @SecurityRequirement(name = "userAuth"),
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "プロジェクトが正常に作成された場合",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = Project.class))
          ),
          @ApiResponse(
              responseCode = "400",
              description = "リクエストの内容が不正（入力値がバリデーション条件違反）だった場合",
              content = @Content()
          ),
          @ApiResponse(
              responseCode = "404",
              description = "認証済みユーザーが削除済みまたは無効化されている場合",
              content = @Content()
          )
      }
  )
  @PostMapping("/projects")
  public ResponseEntity<Project> createProject(
      @AuthenticationPrincipal UserAccountDetails userDetails,
      @Valid @RequestBody ProjectRequest request) {
    Project project = service.createProject(request, userDetails.getAccount().getPublicId());
    return ResponseEntity.status(HttpStatus.CREATED).body(project);
  }

  /**
   * 新規の親タスクを登録します。
   *
   * @param userDetails     現在認証済みのユーザー情報
   * @param projectPublicId 親タスクと紐づくプロジェクトの公開ID
   * @param request         親タスク登録リクエスト
   * @return 作成された親タスク情報
   * @throws RecordNotFoundException 親タスクと紐づくプロジェクトが存在しない場合
   */
  @Operation(
      summary = "新規の親タスク登録",
      description = "新規の親タスクを登録します",
      security = @SecurityRequirement(name = "userAuth"),
      parameters = {
          @Parameter(
              name = "projectPublicId",
              required = true,
              description = "プロジェクトの公開ID（UUID）",
              schema = @Schema(type = "string", format = "uuid",
                  example = "5998fd5d-a2cd-11ef-b71f-6845f15f510c")
          )
      },
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "タスクが正常に作成された場合",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = Task.class))
          ),
          @ApiResponse(
              responseCode = "400",
              description = "リクエストの内容が不正（入力値がバリデーション条件違反）だった場合",
              content = @Content()
          ),
          @ApiResponse(
              responseCode = "404",
              description = "指定した公開IDのプロジェクトが存在しないか、削除されている場合",
              content = @Content()
          )
      }
  )
  @PostMapping("/projects/{projectPublicId}/tasks")
  public ResponseEntity<Task> createParentTask(
      @AuthenticationPrincipal UserAccountDetails userDetails,
      @PathVariable
      @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
          message = "入力の形式に誤りがあります")
      String projectPublicId,
      @Valid @RequestBody TaskRequest request) {
    Task task = service.createParentTask(request, projectPublicId,
        userDetails.getAccount().getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(task);
  }

  /**
   * 新規の子タスクを登録します。
   *
   * @param userDetails  現在認証済みのユーザー情報
   * @param taskPublicId 親タスクの公開ID
   * @param request      子タスク登録リクエスト
   * @return 作成された子タスク情報
   * @throws RecordNotFoundException 親タスクが存在しない場合
   */
  @Operation(
      summary = "新規の子タスク登録",
      description = "新規の子タスクを登録します",
      security = @SecurityRequirement(name = "userAuth"),
      parameters = {
          @Parameter(
              name = "taskPublicId",
              required = true,
              description = "親タスクの公開ID（UUID）",
              schema = @Schema(type = "string", format = "uuid",
                  example = "5998fd5d-a2cd-11ef-b71f-6845f15f510c")
          )
      },
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "タスクが正常に作成された場合",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = Task.class))
          ),
          @ApiResponse(
              responseCode = "400",
              description = "リクエストの内容が不正（入力値がバリデーション条件違反）だった場合",
              content = @Content()
          ),
          @ApiResponse(
              responseCode = "404",
              description = "指定した公開IDの親タスクが存在しないか、削除されている場合",
              content = @Content()
          )
      }
  )
  @PostMapping("/tasks/{taskPublicId}/subtasks")
  public ResponseEntity<Task> createSubtask(
      @AuthenticationPrincipal UserAccountDetails userDetails,
      @PathVariable
      @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
          message = "入力の形式に誤りがあります")
      String taskPublicId,
      @Valid @RequestBody TaskRequest request) {
    Task task = service.createSubtask(request, taskPublicId, userDetails.getAccount().getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(task);
  }

  /**
   * 既存のプロジェクトを更新します。
   *
   * @param userDetails     現在認証済みのユーザー情報
   * @param projectPublicId プロジェクトの公開ID
   * @param request         プロジェクトの更新用リクエスト
   * @return 更新後のプロジェクト情報
   * @throws RecordNotFoundException プロジェクトが存在しない場合
   */
  @Operation(
      summary = "プロジェクト更新",
      description = "既存プロジェクトの内容を更新します。",
      security = @SecurityRequirement(name = "userAuth"),
      parameters = {
          @Parameter(
              name = "projectPublicId",
              required = true,
              description = "プロジェクトの公開ID（UUID）",
              schema = @Schema(type = "string", format = "uuid",
                  example = "5998fd5d-a2cd-11ef-b71f-6845f15f510c")
          )
      },
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "プロジェクトが正常に更新された場合",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = Project.class))
          ),
          @ApiResponse(
              responseCode = "400",
              description = "リクエストの内容が不正（入力値がバリデーション条件違反）だった場合",
              content = @Content()
          ),
          @ApiResponse(
              responseCode = "404",
              description = "指定した公開IDのプロジェクトが存在しないか、削除されている場合",
              content = @Content()
          )
      }
  )
  @PutMapping("/projects/{projectPublicId}")
  public ResponseEntity<Project> updateProject(
      @AuthenticationPrincipal UserAccountDetails userDetails,
      @PathVariable
      @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
          message = "入力の形式に誤りがあります")
      String projectPublicId,
      @Valid @RequestBody ProjectRequest request) {
    Project project = service.updateProject(request, projectPublicId,
        userDetails.getAccount().getId());
    return ResponseEntity.ok(project);
  }

  /**
   * 既存のタスクを更新します。
   *
   * @param taskPublicId タスクの公開ID
   * @param request      タスクの更新用リクエスト
   * @return 更新後のタスク情報
   * @throws RecordNotFoundException タスクが存在しない場合
   */
  @Operation(
      summary = "タスク更新",
      description = "既存タスクの内容を更新します。",
      security = @SecurityRequirement(name = "userAuth"),
      parameters = {
          @Parameter(
              name = "taskPublicId",
              required = true,
              description = "タスクの公開ID（UUID）",
              schema = @Schema(type = "string", format = "uuid",
                  example = "5998fd5d-a2cd-11ef-b71f-6845f15f510c")
          )
      },
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "タスクが正常に更新された場合",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = Task.class))
          ),
          @ApiResponse(
              responseCode = "400",
              description = "リクエストの内容が不正（入力値がバリデーション条件違反）だった場合",
              content = @Content()
          ),
          @ApiResponse(
              responseCode = "404",
              description = "指定した公開IDのタスクが存在しないか、削除されている場合",
              content = @Content()
          )
      }
  )
  @PutMapping("/tasks/{taskPublicId}")
  public ResponseEntity<Task> updateTask(
      @PathVariable
      @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
          message = "入力の形式に誤りがあります")
      String taskPublicId,
      @Valid @RequestBody TaskRequest request) {
    Task task = service.updateTask(request, taskPublicId);
    return ResponseEntity.ok(task);
  }

  /**
   * プロジェクトを論理削除します。
   *
   * @param projectPublicId プロジェクトの公開ID
   * @return 空レスポンス（204 No Content）
   * @throws RecordNotFoundException プロジェクトが存在しない場合
   */
  @Operation(
      summary = "プロジェクトの削除",
      description = "プロジェクトを論理削除します。",
      security = @SecurityRequirement(name = "userAuth"),
      parameters = {
          @Parameter(
              name = "projectPublicId",
              required = true,
              description = "プロジェクトの公開ID（UUID）",
              schema = @Schema(type = "string", format = "uuid",
                  example = "5998fd5d-a2cd-11ef-b71f-6845f15f510c")
          )
      },
      responses = {
          @ApiResponse(
              responseCode = "204",
              description = "削除が成功した場合（レスポンスボディはありません）"
          ),
          @ApiResponse(
              responseCode = "404",
              description = "指定した公開IDのプロジェクトが存在しないか、削除されている場合"
          )
      }
  )
  @DeleteMapping("/projects/{projectPublicId}")
  public ResponseEntity<Void> deleteProject(
      @PathVariable
      @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
          message = "入力の形式に誤りがあります")
      String projectPublicId) {
    service.deleteProject(projectPublicId);
    return ResponseEntity.noContent().build();
  }

  /**
   * タスクを論理削除します。
   *
   * @param taskPublicId タスクの公開ID（UUID形式）
   * @return 空レスポンス（204 No Content）
   * @throws RecordNotFoundException タスクが存在しない場合
   */
  @Operation(
      summary = "タスクの削除",
      description = "タスクを論理削除します。",
      security = @SecurityRequirement(name = "userAuth"),
      parameters = {
          @Parameter(
              name = "taskPublicId",
              required = true,
              description = "タスクの公開ID（UUID）",
              schema = @Schema(type = "string", format = "uuid",
                  example = "5998fd5d-a2cd-11ef-b71f-6845f15f510c")
          )
      },
      responses = {
          @ApiResponse(
              responseCode = "204",
              description = "削除が成功し、レスポンスボディはありません"
          ),
          @ApiResponse(
              responseCode = "404",
              description = "指定した公開IDのタスクが存在しないか、削除されている場合"
          )
      }
  )
  @DeleteMapping("/tasks/{taskPublicId}")
  public ResponseEntity<Void> deleteTask(
      @PathVariable
      @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
          message = "入力の形式に誤りがあります")
      String taskPublicId) {
    service.deleteTask(taskPublicId);
    return ResponseEntity.noContent().build();
  }

}
