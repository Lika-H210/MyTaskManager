package com.portfolio.taskapp.MyTaskManager.domain.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "タスクの優先度")
public enum TaskPriority {
  HIGH,
  MEDIUM,
  LOW
}
