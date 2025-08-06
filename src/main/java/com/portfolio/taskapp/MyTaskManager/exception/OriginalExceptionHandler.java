package com.portfolio.taskapp.MyTaskManager.exception;

import java.util.LinkedHashMap;
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

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex) {
    // 開発者向けログ出力
    log.warn("Validation error occurred", ex);

    // [field:エラーメッセージ]の一覧を作成（ただし同一fieldで複数エラーの場合は最初のメッセージのみ返す）
    Map<String, String> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            fieldError -> Optional.ofNullable(fieldError.getDefaultMessage()).orElse(""),
            (existing, replacement) -> existing
        ));

    Map<String, Object> responseBody = new LinkedHashMap<>();
    responseBody.put("status", HttpStatus.BAD_REQUEST.value());
    responseBody.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
    responseBody.put("detail", errors);

    return ResponseEntity.badRequest().body(responseBody);
  }

}
