package com.portfolio.taskapp.MyTaskManager.controller;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Project;
import com.portfolio.taskapp.MyTaskManager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
          @Parameter(name = "userPublicId",
              required = true,
              description = "ユーザーの公開ID（UUID）(ログイン手法の方針により変更する可能性があります。)",
              schema = @Schema(type = "string", format = "uuid",
                  example = "5998fd5d-a2cd-11ef-b71f-6845f15f510c")
          )},
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "リクエストが正常に処理された場合",
              content = @Content(mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = Project.class)))
          )}
  )
  @GetMapping("/my-project")
  public List<Project> getProjectList(@RequestParam String userPublicId) {
    // TODO: 本番ではトークンから取得するように変更
    return service.getUserProjects(userPublicId);
  }

}
