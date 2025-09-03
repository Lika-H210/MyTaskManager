package com.portfolio.taskapp.MyTaskManager.user.dto.update;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AccountEmailUpdateRequestTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void Email更新で入力チェックに抵触しないこと() {
    AccountEmailUpdateRequest response = new AccountEmailUpdateRequest("new@emaie.com");

    Set<ConstraintViolation<AccountEmailUpdateRequest>> violations = validator.validate(
        response);

    assertThat(violations).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("emailUpdateInvalidPattern")
  void Email更新のリクエストで入力チェックに抵触し例外がスローされていること(
      AccountEmailUpdateRequest request, String errorMessage) {

    Set<ConstraintViolation<AccountEmailUpdateRequest>> violations = validator
        .validate(request);

    assertThat(violations.size()).isGreaterThanOrEqualTo(1);
    assertThat(violations).extracting("message").contains(errorMessage);
  }

  private static Stream<Arguments> emailUpdateInvalidPattern() {
    return Stream.of(
        Arguments.of(new AccountEmailUpdateRequest(""),
            "更新するメルアドレスを入力してください。"),
        Arguments.of(new AccountEmailUpdateRequest("new@"),
            "正しいメールアドレス形式で入力してください"),
        Arguments.of(new AccountEmailUpdateRequest("a".repeat(101)),
            "メールアドレスは100文字以下で入力してください")
    );
  }
}
