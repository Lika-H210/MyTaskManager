package com.portfolio.taskapp.MyTaskManager.exception.handler;

import com.portfolio.taskapp.MyTaskManager.exception.custom.InvalidPasswordChangeException;
import com.portfolio.taskapp.MyTaskManager.exception.custom.NotUniqueException;
import com.portfolio.taskapp.MyTaskManager.exception.custom.RecordNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class OriginalExceptionHandler {

  @ExceptionHandler(NotUniqueException.class)
  public ResponseEntity<Map<String, Object>> handleNotUniqueException(NotUniqueException ex) {
    // 開発者向けログ出力
    log.warn("Duplicate value error: {}", ex.getMessage());

    //表示内容
    HttpStatus status = HttpStatus.BAD_REQUEST;
    Map<String, String> detail = Map.of(ex.getField(), ex.getMessage());
    Map<String, Object> responseBody = createErrorBody(status, detail);

    return ResponseEntity.badRequest().body(responseBody);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {
    // 開発者向けログ出力
    log.warn("Request body validation error occurred: {} fields invalid",
        ex.getBindingResult().getFieldErrors().size());

    // [field:エラーメッセージ]の一覧を作成
    List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

    Map<String, String> errorsMap = new HashMap<>();

    // "field" + "エラーメッセージのリスト" のマップに変換
    Map<String, List<String>> errorsByField = fieldErrors.stream()
        .collect(Collectors.groupingBy(
            FieldError::getField,
            LinkedHashMap::new,
            Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
        ));

    // レスポンス用のエラーメッセージに変換（エラーメッセージのリストから表示するメッセージを一つ選別）
    Map<String, String> finalErrors = new LinkedHashMap<>();
    errorsByField.forEach((field, messages) -> {
      String selectMassage = messages.stream()
          .filter(msg -> msg.contains("必須"))
          .findFirst()
          .orElse(messages.getFirst());
      finalErrors.put(field, selectMassage);
    });

    HttpStatus status = HttpStatus.BAD_REQUEST;
    Map<String, Object> responseBody = createErrorBody(status, finalErrors);

    return ResponseEntity.badRequest().body(responseBody);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex) {
    // 開発者向けログ出力
    log.warn("Value type error occurred: {}", ex.getMessage(), ex);

    HttpStatus status = HttpStatus.BAD_REQUEST;
    String detail = ex.getMessage();
    Map<String, Object> responseBody = createErrorBody(status, detail);

    return ResponseEntity.badRequest().body(responseBody);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, Object>> handleConstraintViolationException(
      ConstraintViolationException ex) {

    log.warn("Request parameter validation error occurred: {}", ex.getMessage(), ex);

    HttpStatus status = HttpStatus.BAD_REQUEST;
    Map<String, String> detail = ex.getConstraintViolations().stream()
        .collect(Collectors.toMap(
            cv -> cv.getPropertyPath().toString(),
            ConstraintViolation::getMessage
        ));
    Map<String, Object> responseBody = createErrorBody(status, detail);

    return ResponseEntity.status(status).body(responseBody);
  }

  @ExceptionHandler(RecordNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(
      RecordNotFoundException ex) {
    // 開発者向けログ出力
    log.info("Record not found: {}", ex.getMessage());

    //表示内容
    Map<String, Object> responseBody = createErrorBody(ex.getHttpStatus(), ex.getMessage());

    return ResponseEntity.status(ex.getHttpStatus()).body(responseBody);
  }

  @ExceptionHandler(InvalidPasswordChangeException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidPasswordChangeException(
      InvalidPasswordChangeException ex) {
    // 開発者向けログ出力
    log.warn("Password update failed: {}", ex.getMessage());

    //表示内容
    Map<String, String> fieldError = Map.of(ex.getField(), ex.getMessage());
    Map<String, Object> responseBody = createErrorBody(ex.getHttpStatus(), fieldError);

    return ResponseEntity.status(ex.getHttpStatus()).body(responseBody);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
    // 開発者向けログ出力
    log.error("Unexpected error occurred", ex);

    //表示内容
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    String detail = "An unexpected error occurred. Please contact support.";
    Map<String, Object> responseBody = createErrorBody(status, detail);

    return ResponseEntity.status(status).body(responseBody);
  }

  private static Map<String, Object> createErrorBody(HttpStatus status, Object detail) {
    Map<String, Object> responseBody = new LinkedHashMap<>();
    responseBody.put("status", status.value());
    responseBody.put("error", status.getReasonPhrase());
    responseBody.put("detail", detail);
    return responseBody;
  }

}
