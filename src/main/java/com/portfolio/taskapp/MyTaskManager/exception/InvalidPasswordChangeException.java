package com.portfolio.taskapp.MyTaskManager.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class InvalidPasswordChangeException extends Exception {

  @Getter
  private final HttpStatus httpStatus;

  public InvalidPasswordChangeException(String message) {
    super(message);
    this.httpStatus = HttpStatus.UNAUTHORIZED;
  }
}
