package com.portfolio.taskapp.MyTaskManager.user.model;

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

class AccountUpdateRequestTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void ユーザー更新処理のリクエストで入力チェックに抵触しないこと() {
    AccountUpdateRequest request = createUpdateRequest();

    Set<ConstraintViolation<AccountUpdateRequest>> validations = validator.validate(request);

    assertThat(validations).isEmpty();
  }

  @Test
  void ユーザー更新処理のリクエストで各フィールドでnullが許容されていること() {
    AccountUpdateRequest request = new AccountUpdateRequest(null, null, null, null);

    Set<ConstraintViolation<AccountUpdateRequest>> validations = validator.validate(request);

    assertThat(validations).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("requestEmptyOrBlankPattern")
  void ユーザー更新処理のリクエストでfieldが空及びスペースのみの場合に入力チェック抵触し例外がスローされていること(
      AccountUpdateRequest request) {
    Set<ConstraintViolation<AccountUpdateRequest>> validations = validator.validate(request);

    assertThat(validations).isNotEmpty();
  }

  private static Stream<Arguments> requestEmptyOrBlankPattern() {
    return Stream.of(
        Arguments.of(withInvalidName("")),
        Arguments.of(withInvalidName(" ")),
        Arguments.of(withInvalidEmail("")),
        Arguments.of(withInvalidEmail(" ")),
        Arguments.of(withInvalidOldPassword("")),
        Arguments.of(withInvalidNewPassword("")),
        Arguments.of(withInvalidNewPassword(" ".repeat(8)))
    );
  }

  @ParameterizedTest
  @MethodSource("generalInvalidFieldCases")
  void ユーザー更新処理のリクエストで入力チェックに抵触し例外がスローされていること(
      AccountUpdateRequest request, String errorMessage) {

    Set<ConstraintViolation<AccountUpdateRequest>> validations = validator.validate(request);

    assertThat(validations).isNotEmpty();
    assertThat(validations).extracting("message").contains(errorMessage);
  }

  private static Stream<Arguments> generalInvalidFieldCases() {

    return Stream.of(
        Arguments.of(withInvalidName("a".repeat(51)),
            "ユーザー名は50字以内で入力してください"),
        Arguments.of(withInvalidEmail("aaa@"),
            "正しいメールアドレス形式で入力してください"),
        Arguments.of(withInvalidNewPassword("pass123"),
            "パスワードは8文字以上50文字以下で入力してください"),
        Arguments.of(withInvalidNewPassword("ひらがなカタカナ漢字不可"),
            "パスワードは英数字と記号(!@#$%^&*()_+-=)のみ使用可能です")
    );
  }


  private static AccountUpdateRequest createUpdateRequest() {
    return new AccountUpdateRequest(
        "テスト Name 太郎",
        "test@email.com",
        "password",
        "azAZ09!@#$%^&*()_+-=");
  }

  private static AccountUpdateRequest withInvalidName(String invalidUserName) {
    return new AccountUpdateRequest(
        invalidUserName,
        null,
        null,
        null);
  }

  private static AccountUpdateRequest withInvalidEmail(String invalidUserEmail) {
    return new AccountUpdateRequest(
        null,
        invalidUserEmail,
        null,
        null);
  }

  private static AccountUpdateRequest withInvalidOldPassword(String invalidOldPassword) {
    return new AccountUpdateRequest(
        null,
        null,
        invalidOldPassword,
        null);
  }

  private static AccountUpdateRequest withInvalidNewPassword(String invalidNewPassword) {
    return new AccountUpdateRequest(
        null,
        null,
        null,
        invalidNewPassword);
  }
}
