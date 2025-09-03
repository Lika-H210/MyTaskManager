package com.portfolio.taskapp.MyTaskManager.user.dto.update;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AccountPasswordUpdateRequestTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  private static final String VALID_CURRENT_PASSWORD = "Password";
  private static final String VALID_NEW_PASSWORD = "NewPass123!%-_";

  @ParameterizedTest
  @MethodSource("passwordUpdateValidPattern")
  void Password更新で入力チェックに抵触しないこと(AccountPasswordUpdateRequest request) {
    Set<ConstraintViolation<AccountPasswordUpdateRequest>> violations = validator.validate(
        request);

    assertThat(violations).isEmpty();
  }

  private static Stream<Arguments> passwordUpdateValidPattern() {
    return Stream.of(
        Arguments.of(new AccountPasswordUpdateRequest(VALID_CURRENT_PASSWORD, VALID_NEW_PASSWORD)),
        Arguments.of(new AccountPasswordUpdateRequest("short", VALID_NEW_PASSWORD)),
        Arguments.of(
            new AccountPasswordUpdateRequest(VALID_CURRENT_PASSWORD, "azAZ09!@#$%^&*()_+-="))
    );
  }

  @ParameterizedTest
  @MethodSource("passwordUpdateInvalidPattern")
  void Password更新のリクエストで入力チェックに抵触し例外がスローされていること(
      AccountPasswordUpdateRequest request, String errorMessage) {

    Set<ConstraintViolation<AccountPasswordUpdateRequest>> violations = validator
        .validate(request);

    assertThat(violations.size()).isGreaterThanOrEqualTo(1);
    assertThat(violations).extracting("message").contains(errorMessage);
  }

  private static Stream<Arguments> passwordUpdateInvalidPattern() {
    return Stream.of(
        Arguments.of(new AccountPasswordUpdateRequest("", VALID_NEW_PASSWORD),
            "現在のパスワードを入力してください"),
        Arguments.of(new AccountPasswordUpdateRequest(VALID_CURRENT_PASSWORD, ""),
            "新しいパスワードを入力してください"),
        Arguments.of(new AccountPasswordUpdateRequest(VALID_CURRENT_PASSWORD, "1234567"),
            "パスワードは8文字以上50文字以下で入力してください"),
        Arguments.of(new AccountPasswordUpdateRequest(VALID_CURRENT_PASSWORD, "ああああああああ"),
            "パスワードは英数字と記号(!@#$%^&*()_+-=)のみ使用可能です")
    );
  }
}
