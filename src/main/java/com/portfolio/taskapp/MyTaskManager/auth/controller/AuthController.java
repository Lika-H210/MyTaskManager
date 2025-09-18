package com.portfolio.taskapp.MyTaskManager.auth.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

  @GetMapping("/csrf-token")
  public CsrfToken getCsrfToken(CsrfToken token) {
    return token;
  }

}
