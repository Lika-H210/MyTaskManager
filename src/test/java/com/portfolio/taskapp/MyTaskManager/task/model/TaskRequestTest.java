package com.portfolio.taskapp.MyTaskManager.task.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.portfolio.taskapp.MyTaskManager.domain.enums.TaskPriority;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TaskRequestTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  private static final String VALID_CAPTION = "task名";
  private static final String VALID_DESCRIPTION = "description";
  private static final LocalDate VALID_DUE_DATE = LocalDate.now().plusDays(7);
  private static final int VALID_ESTIMATE_TIME = 1;
  private static final int VALID_ACTUAL_TIME = 0;
  private static final int VALID_PROGRESS = 0;
  private static final TaskPriority VALID_PRIORITY = TaskPriority.LOW;

  @Test
  void タスクのリクエストで入力チェックに抵触しないこと() {
    TaskRequest request = new TaskRequest(
        VALID_CAPTION,
        VALID_DESCRIPTION,
        VALID_DUE_DATE,
        VALID_ESTIMATE_TIME,
        VALID_ACTUAL_TIME,
        VALID_PROGRESS,
        VALID_PRIORITY);

    Set<ConstraintViolation<TaskRequest>> validators = validator.validate(request);

    assertThat(validators).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidFieldCases")
  void タスクのリクエストで入力チェックに抵触し例外がスローされていること(TaskRequest request,
      String message) {
    Set<ConstraintViolation<TaskRequest>> validators = validator.validate(request);

    assertThat(validators).isNotEmpty();
    assertThat(validators).extracting("message").contains(message);
  }

  private static Stream<Arguments> invalidFieldCases() {
    return Stream.of(
        Arguments.of(withInvalidCaption(null),
            "タスク名は必須です。"),
        Arguments.of(withInvalidCaption(""),
            "タスク名は必須です。"),
        Arguments.of(withInvalidCaption(" "),
            "タスク名は必須です。"),
        Arguments.of(withInvalidCaption("a".repeat(101)),
            "タスク名は100字以内で入力してください"),
        Arguments.of(withInvalidDescription(null),
            "タスクの詳細説明はNullを許容しません。未入力は空文字にしてください。"),
        Arguments.of(withInvalidDescription("a".repeat(1001)),
            "タスクの詳細説明は1000字以内で入力してください"),
        Arguments.of(withInvalidDueDate(null),
            "期限日を設定してください"),
        Arguments.of(withInvalidEstimatedTime(0),
            "入力値は分単位で正の整数値を入力してください"),
        Arguments.of(withInvalidActualTime(-1),
            "入力値は分単位で正の整数値を入力してください"),
        Arguments.of(withInvalidProgress(-1),
            "入力値は0以上の整数値を入力してください"),
        Arguments.of(withInvalidProgress(101),
            "入力値は100以下の整数値を入力してください"),
        Arguments.of(withInvalidPriority(null),
            "優先度は必須です")
    );
  }

  private static TaskRequest withInvalidCaption(String invalidCaption) {
    return new TaskRequest(
        invalidCaption,
        VALID_DESCRIPTION,
        VALID_DUE_DATE,
        VALID_ESTIMATE_TIME,
        VALID_ACTUAL_TIME,
        VALID_PROGRESS,
        VALID_PRIORITY);
  }

  private static TaskRequest withInvalidDescription(String invalidDescription) {
    return new TaskRequest(
        VALID_CAPTION,
        invalidDescription,
        VALID_DUE_DATE,
        VALID_ESTIMATE_TIME,
        VALID_ACTUAL_TIME,
        VALID_PROGRESS,
        VALID_PRIORITY);
  }

  private static TaskRequest withInvalidDueDate(LocalDate invalidDueDate) {
    return new TaskRequest(
        VALID_CAPTION,
        VALID_DESCRIPTION,
        invalidDueDate,
        VALID_ESTIMATE_TIME,
        VALID_ACTUAL_TIME,
        VALID_PROGRESS,
        VALID_PRIORITY);
  }

  private static TaskRequest withInvalidEstimatedTime(int invalidEstimatedTime) {
    return new TaskRequest(
        VALID_CAPTION,
        VALID_DESCRIPTION,
        VALID_DUE_DATE,
        invalidEstimatedTime,
        VALID_ACTUAL_TIME,
        VALID_PROGRESS,
        VALID_PRIORITY);
  }

  private static TaskRequest withInvalidActualTime(int invalidActualTime) {
    return new TaskRequest(
        VALID_CAPTION,
        VALID_DESCRIPTION,
        VALID_DUE_DATE,
        VALID_ESTIMATE_TIME,
        invalidActualTime,
        VALID_PROGRESS,
        VALID_PRIORITY);
  }

  private static TaskRequest withInvalidProgress(int invalidProgress) {
    return new TaskRequest(
        VALID_CAPTION,
        VALID_DESCRIPTION,
        VALID_DUE_DATE,
        VALID_ESTIMATE_TIME,
        VALID_ACTUAL_TIME,
        invalidProgress,
        VALID_PRIORITY);
  }

  private static TaskRequest withInvalidPriority(TaskPriority invalidPriority) {
    return new TaskRequest(
        VALID_CAPTION,
        VALID_DESCRIPTION,
        VALID_DUE_DATE,
        VALID_ESTIMATE_TIME,
        VALID_ACTUAL_TIME,
        VALID_PROGRESS,
        invalidPriority);
  }
}
