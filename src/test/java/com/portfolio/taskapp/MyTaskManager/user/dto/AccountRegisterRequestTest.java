package com.portfolio.taskapp.MyTaskManager.user.dto;

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

class AccountRegisterRequestTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  private static final String VALID_USER_NAME = "テスト Name 太郎";
  private static final String VALID_EMAIL = "test@email.com";
  private static final String VALID_PASSWORD = "azAZ09!@#$%^&*()_+-=";

  @Test
  void ユーザー登録処理のリクエストで入力チェックに抵触しないこと() {
    AccountRegisterRequest request = createRegisterRequest();

    Set<ConstraintViolation<AccountRegisterRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("accountRegisterInvalidPattern")
  void ユーザー登録処理のリクエストで入力チェックに抵触し例外がスローされていること(
      AccountRegisterRequest request, String errorMessage) {

    Set<ConstraintViolation<AccountRegisterRequest>> violations = validator.validate(request);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message").contains(errorMessage);
  }

  private static Stream<Arguments> accountRegisterInvalidPattern() {

    return Stream.of(
        Arguments.of(withInvalidName(" "),
            "ユーザー名は必須です"),
        Arguments.of(withInvalidName("a".repeat(51)),
            "ユーザー名は50文字以下で入力してください"),
        Arguments.of(withInvalidEmail(""),
            "メールアドレスは必須です"),
        Arguments.of(withInvalidEmail("aaa@"),
            "正しいメールアドレス形式で入力してください"),
        Arguments.of(withInvalidPassword(null),
            "パスワードは必須です"),
        Arguments.of(withInvalidPassword("pass123"),
            "パスワードは8文字以上50文字以下で入力してください"),
        Arguments.of(withInvalidPassword("ひらがなカタカナ漢字不可"),
            "パスワードに使えない文字が含まれています。")
    );
  }


  private AccountRegisterRequest createRegisterRequest() {
    return new AccountRegisterRequest(
        VALID_USER_NAME,
        VALID_EMAIL,
        VALID_PASSWORD);
  }

  private static AccountRegisterRequest withInvalidName(String invalidUserName) {
    return new AccountRegisterRequest(
        invalidUserName,
        VALID_EMAIL,
        VALID_PASSWORD);
  }

  private static AccountRegisterRequest withInvalidEmail(String invalidEmail) {
    return new AccountRegisterRequest(
        VALID_USER_NAME,
        invalidEmail,
        VALID_PASSWORD);
  }

  private static AccountRegisterRequest withInvalidPassword(String invalidPassword) {
    return new AccountRegisterRequest(
        VALID_USER_NAME,
        VALID_EMAIL,
        invalidPassword);
  }

}
