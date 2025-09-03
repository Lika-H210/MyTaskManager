package com.portfolio.taskapp.MyTaskManager.task.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.portfolio.taskapp.MyTaskManager.domain.enums.ProjectStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ProjectRequestTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  private static final String VALID_CAPTION = "Project名";
  private static final String VALID_DESCRIPTION = "description";
  private static final ProjectStatus VALID_STATUS = ProjectStatus.ACTIVE;

  @Test
  void プロジェクトのリクエストで入力チェックに抵触しないこと() {
    ProjectRequest request = new ProjectRequest(VALID_CAPTION, VALID_DESCRIPTION, VALID_STATUS);

    Set<ConstraintViolation<ProjectRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("requestFieldInvalidPattern")
  void プロジェクトのリクエストで入力チェックに抵触し例外がスローされていること(
      ProjectRequest request, String message) {
    Set<ConstraintViolation<ProjectRequest>> violations = validator.validate(request);

    assertThat(violations.size()).isGreaterThan(0);
    assertThat(violations).extracting("message").contains(message);
  }

  private static Stream<Arguments> requestFieldInvalidPattern() {
    return Stream.of(
        Arguments.of(withInvalidCaption(null),
            "プロジェクト名は必須です"),
        Arguments.of(withInvalidCaption(" "),
            "プロジェクト名は必須です"),
        Arguments.of(withInvalidCaption("a".repeat(101)),
            "プロジェクト名は文字数を100字以内で入力してください"),
        Arguments.of(new ProjectRequest(VALID_CAPTION, null, VALID_STATUS),
            "プロジェクトの詳細説明はNullを許容しません。未入力は空文字にしてください。"),
        Arguments.of(new ProjectRequest(VALID_CAPTION, VALID_DESCRIPTION, null),
            "ステータスは必須です")
    );
  }

  private static ProjectRequest withInvalidCaption(String invalidCaption) {
    return new ProjectRequest(
        invalidCaption,
        VALID_DESCRIPTION,
        VALID_STATUS);
  }

}
