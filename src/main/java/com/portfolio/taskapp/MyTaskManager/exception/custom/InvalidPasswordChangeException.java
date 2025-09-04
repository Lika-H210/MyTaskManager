package com.portfolio.taskapp.MyTaskManager.exception.custom;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidPasswordChangeException extends Exception {

  private final String field;
  private final HttpStatus httpStatus;


  public InvalidPasswordChangeException(String field, String message) {
    super(message);
    this.field = field;
    this.httpStatus = HttpStatus.BAD_REQUEST;
  }
}
