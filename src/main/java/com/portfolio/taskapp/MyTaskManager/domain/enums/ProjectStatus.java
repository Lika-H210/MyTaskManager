package com.portfolio.taskapp.MyTaskManager.domain.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "プロジェクトの達成状況")
public enum ProjectStatus {
  ACTIVE,
  ARCHIVED
}
