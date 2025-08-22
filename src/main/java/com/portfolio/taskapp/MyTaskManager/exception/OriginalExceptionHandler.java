package com.portfolio.taskapp.MyTaskManager.exception;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    log.warn("duplicate value error: {}", ex.getMessage());

    //表示内容
    HttpStatus status = HttpStatus.BAD_REQUEST;
    Map<String, String> detail = Map.of(ex.getField(), ex.getMessage());
    Map<String, Object> responseBody = createErrorBody(status, detail);

    return ResponseEntity.badRequest().body(responseBody);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex) {
    // 開発者向けログ出力
    log.warn("validation error occurred");

    // [field:エラーメッセージ]の一覧を作成
    Map<String, List<String>> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(Collectors.groupingBy(
            FieldError::getField,
            LinkedHashMap::new,
            Collectors.mapping(
                fieldError -> Optional.ofNullable(fieldError.getDefaultMessage()).orElse(""),
                Collectors.toList()
            )
        ));

    HttpStatus status = HttpStatus.BAD_REQUEST;
    Map<String, Object> responseBody = createErrorBody(status, errors);

    return ResponseEntity.badRequest().body(responseBody);
  }

  @ExceptionHandler(RecordNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(
      RecordNotFoundException ex) {
    // 開発者向けログ出力
    log.info("record not found: {}", ex.getMessage());

    //表示内容
    Map<String, Object> responseBody = createErrorBody(ex.getHttpStatus(), ex.getMessage());

    return ResponseEntity.status(ex.getHttpStatus()).body(responseBody);
  }

  @ExceptionHandler(InvalidPasswordChangeException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidPasswordChangeException(
      InvalidPasswordChangeException ex) {
    // 開発者向けログ出力
    log.warn("password update failed: {}", ex.getMessage());

    //表示内容
    Map<String, Object> responseBody = createErrorBody(ex.getHttpStatus(), ex.getMessage());

    return ResponseEntity.status(ex.getHttpStatus()).body(responseBody);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
    // 開発者向けログ出力
    log.error("unexpected error has occurred", ex);

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
