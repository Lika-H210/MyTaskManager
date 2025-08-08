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
    log.warn("Duplicate value error: {}", ex.getMessage(), ex);

    //表示内容
    HttpStatus status = HttpStatus.BAD_REQUEST;
    Map<String, Object> responseBody = new LinkedHashMap<>();
    responseBody.put("status", status.value());
    responseBody.put("error", status);
    responseBody.put("detail", Map.of(ex.getField(), ex.getMessage()));

    return ResponseEntity.badRequest().body(responseBody);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex) {
    // 開発者向けログ出力
    log.warn("Validation error occurred", ex);

    // [field:エラーメッセージ]の一覧を作成（ただし同一fieldで複数エラーの場合は最初のメッセージのみ返す）
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

    Map<String, Object> responseBody = new LinkedHashMap<>();
    responseBody.put("status", HttpStatus.BAD_REQUEST.value());
    responseBody.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
    responseBody.put("detail", errors);

    return ResponseEntity.badRequest().body(responseBody);
  }

  @ExceptionHandler(RecordNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(
      RecordNotFoundException ex) {
    // 開発者向けログ出力
    log.warn("resource not fount: {}", ex.getMessage(), ex);

    //表示内容
    Map<String, Object> responseBody = new LinkedHashMap<>();
    responseBody.put("status", ex.getHttpStatus().value());
    responseBody.put("error", ex.getHttpStatus());
    responseBody.put("detail", ex.getMessage());

    return ResponseEntity.status(ex.getHttpStatus()).body(responseBody);
  }
}
