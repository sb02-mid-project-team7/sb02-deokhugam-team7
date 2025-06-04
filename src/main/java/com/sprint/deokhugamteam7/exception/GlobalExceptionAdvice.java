package com.sprint.deokhugamteam7.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionAdvice {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleDeokhugamException(DeokhugamException e) {
        HttpStatus status = e.getErrorCode().getHttpStatus();
        return ResponseEntity
            .status(status)
            .body(ErrorResponse.fromDeokhugamException(e));
    }

  // api 요청 시 @Valid 예외처리 담당
  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    return ResponseEntity
        .status(e.getStatusCode())
        .body(ErrorResponse.fromMethodArgumentNotValidException(e));
  }

  // api 요청 시 타입 변환 에러 담당
  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity
        .status(status)
        .body(ErrorResponse.fromArgumentTypeMismatchException(e, status));
  }

}
