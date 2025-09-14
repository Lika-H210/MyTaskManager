package com.portfolio.taskapp.MyTaskManager.exception.custom;

import com.portfolio.taskapp.MyTaskManager.exception.custom.enums.TargetResource;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidOwnerAccessException extends RuntimeException {

  private final HttpStatus httpStatus;
  private final TargetResource targetResource;

  public InvalidOwnerAccessException(TargetResource targetResource) {
    super("no permission on " + targetResource.toString());
    this.targetResource = targetResource;
    this.httpStatus = HttpStatus.FORBIDDEN;
  }

}
