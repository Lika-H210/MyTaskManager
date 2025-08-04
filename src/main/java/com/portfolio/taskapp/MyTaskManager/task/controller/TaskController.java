package com.portfolio.taskapp.MyTaskManager.task.controller;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Project;
import com.portfolio.taskapp.MyTaskManager.domain.entity.Task;
import com.portfolio.taskapp.MyTaskManager.task.model.ProjectRequest;
import com.portfolio.taskapp.MyTaskManager.task.model.TaskRequest;
import com.portfolio.taskapp.MyTaskManager.task.model.TaskTree;
import com.portfolio.taskapp.MyTaskManager.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class TaskController {

  private TaskService service;

  @Autowired
  public TaskController(TaskService service) {
    this.service = service;
  }

  @Operation(
      summary = "ユーザープロジェクトの一覧取得",
      description = "ユーザーのid情報に紐づくプロジェクト情報の一覧を取得します",
      parameters = {
          @Parameter(
              name = "userPublicId",
              required = true,
              description = "ユーザーの公開ID（UUID）(ログイン手法の方針により変更する可能性があります。)",
              schema = @Schema(type = "string", format = "uuid",
                  example = "5998fd5d-a2cd-11ef-b71f-6845f15f510c")
          )
      },
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "リクエストが正常に処理された場合",
              content = @Content(mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = Project.class)))
          )
      }
  )
  @GetMapping("/my-project")
  public List<Project> getProjectList(@RequestParam String userPublicId) {
    // TODO: 本番ではトークンから取得するように変更
    return service.getUserProjects(userPublicId);
  }

  @Operation(
      summary = "プロジェクトの親子タスク一覧取得",
      description = "プロジェクトのid情報に紐づく全親子タスク情報の一覧を取得します。",
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
          )
      }
  )
  @GetMapping("/projects/{projectPublicId}/tasks")
  public List<TaskTree> getTaskTreeListByProjectPublicId(@PathVariable String projectPublicId) {
    return service.getTasksByProjectPublicId(projectPublicId);
  }

  @Operation(
      summary = "単独の親子タスク取得",
      description = "親タスクと当該親タスクに紐づく全子タスク情報を取得します。",
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
          )
      }
  )
  @GetMapping("/tasks/{taskPublicId}")
  public TaskTree getTaskTree(@PathVariable String taskPublicId) {
    return service.getTaskTreeByTaskPublicId(taskPublicId);
  }

  @Operation(
      summary = "新規プロジェクト登録",
      description = "新規のプロジェクトを登録します。",
      parameters = {
          @Parameter(
              name = "userPublicId",
              required = true,
              description = "ユーザーの公開ID（UUID）",
              schema = @Schema(type = "string", format = "uuid",
                  example = "5998fd5d-a2cd-11ef-b71f-6845f15f510c")
          )
      },
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "プロジェクトが正常に作成された場合",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = Project.class))
          )
      }
  )
  @PostMapping("/users/{userPublicId}/projects")
  public ResponseEntity<Project> createProject(
      @PathVariable
      @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
          message = "入力の形式に誤りがあります")
      String userPublicId,
      @Validated @RequestBody ProjectRequest request) {

    Project project = service.createProject(request, userPublicId);

    return ResponseEntity.status(HttpStatus.CREATED).body(project);
  }

  @Operation(
      summary = "新規の親タスク登録",
      description = "新規の親タスクを登録します",
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
          )
      }
  )
  @PostMapping("/projects/{projectPublicId}/tasks")
  public ResponseEntity<Task> createParentTask(
      @PathVariable
      @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
          message = "入力の形式に誤りがあります")
      String projectPublicId,
      @Validated @RequestBody TaskRequest request) {
    Task task = service.createParentTask(request, projectPublicId);
    return ResponseEntity.status(HttpStatus.CREATED).body(task);
  }

  @Operation(
      summary = "プロジェクト更新",
      description = "既存プロジェクトの内容を更新します。",
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
          )
      }
  )
  @PutMapping("/projects/{projectPublicId}")
  public ResponseEntity<Project> updateProject(
      @PathVariable
      @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
          message = "入力の形式に誤りがあります")
      String projectPublicId,
      @Validated @RequestBody ProjectRequest request) {
    Project project = service.updateProject(request, projectPublicId);
    return ResponseEntity.ok(project);
  }

}
