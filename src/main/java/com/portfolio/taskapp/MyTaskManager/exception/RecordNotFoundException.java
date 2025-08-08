package com.portfolio.taskapp.MyTaskManager.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class RecordNotFoundException extends RuntimeException {

  @Getter
  private final HttpStatus httpStatus;

  public RecordNotFoundException(String message) {
    super(message);
    this.httpStatus = HttpStatus.NOT_FOUND;
  }

}
