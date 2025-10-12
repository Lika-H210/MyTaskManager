package com.portfolio.taskapp.MyTaskManager.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

  @Operation(
      summary = "CSRFトークンの取得",
      description = "CSRF保護付きリクエストに使用するトークンを取得します。",
      security = @SecurityRequirement(name = "userAuth"),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "CSRFトークンが正常に取得された場合",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = org.springframework.security.web.csrf.CsrfToken.class)
              )
          )
      }
  )
  @GetMapping("/csrf-token")
  public CsrfToken getCsrfToken(CsrfToken token) {
    return token;
  }

}
