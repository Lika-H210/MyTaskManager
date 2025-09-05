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

class AccountUserInfoUpdateRequestTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void ユーザー情報更新で入力チェックに抵触しないこと() {
    AccountUserInfoUpdateRequest response = new AccountUserInfoUpdateRequest("New Name");

    Set<ConstraintViolation<AccountUserInfoUpdateRequest>> violations = validator.validate(
        response);

    assertThat(violations).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("userInfoUpdateInvalidPattern")
  void ユーザー情報更新のリクエストで入力チェックに抵触し例外がスローされていること(
      AccountUserInfoUpdateRequest request, String errorMessage) {

    Set<ConstraintViolation<AccountUserInfoUpdateRequest>> violations = validator
        .validate(request);

    assertThat(violations.size()).isGreaterThanOrEqualTo(1);
    assertThat(violations).extracting("message").contains(errorMessage);
  }

  private static Stream<Arguments> userInfoUpdateInvalidPattern() {
    return Stream.of(
        Arguments.of(new AccountUserInfoUpdateRequest(" "),
            "ユーザー名は必須です"),
        Arguments.of(new AccountUserInfoUpdateRequest("a".repeat(51)),
            "ユーザー名は50文字以下で入力してください")
    );
  }
}