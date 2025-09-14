package com.portfolio.taskapp.MyTaskManager.exception.custom.enums;

public enum TargetResource {
  USER("user"),
  PROJECT("project"),
  TASK("task");

  private final String displayName;

  TargetResource(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String toString() {
    return displayName;
  }
}
