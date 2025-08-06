package com.portfolio.taskapp.MyTaskManager.exception;

import lombok.Getter;

@Getter
public class NotUniqueException extends Exception {

  private final String field;

  public NotUniqueException(String field, String message) {
    super(message);
    this.field = field;
  }

}
