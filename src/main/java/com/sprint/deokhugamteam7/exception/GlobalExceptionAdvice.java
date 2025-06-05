package com.sprint.deokhugamteam7.exception;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionAdvice {

  @ExceptionHandler(DeokhugamException.class)
  public ResponseEntity<ErrorResponse> handleDeokhugamException(DeokhugamException e) {
    HttpStatus status = e.getErrorCode().getHttpStatus();
    return ResponseEntity
        .status(status)
        .body(ErrorResponse.fromDeokhugamException(e));
  }

  @ExceptionHandler({
      MethodArgumentNotValidException.class,
      MethodArgumentTypeMismatchException.class,
      MissingPathVariableException.class,
      MissingServletRequestParameterException.class,
      MissingRequestHeaderException.class,
      HttpMessageNotReadableException.class
  })
  public ResponseEntity<ErrorResponse> handleClientRequestExceptions(Exception e) {
    if (e instanceof MethodArgumentNotValidException ex) {
      log.error("{},{}",e.getClass(), e.getMessage());
      return ResponseEntity.badRequest().body(ErrorResponse.fromMethodArgumentNotValidException(ex));
    }
    if (e instanceof MethodArgumentTypeMismatchException ex) {
      log.error("{},{}",e.getClass(), e.getMessage());
      return ResponseEntity.badRequest().body(ErrorResponse.fromArgumentTypeMismatchException(ex));
    }
    if (e instanceof MissingPathVariableException ex) {
      log.error("{},{}",e.getClass(), e.getMessage());
      return ResponseEntity.badRequest().body(ErrorResponse.fromMissingPathVariableException(ex));
    }
    if (e instanceof MissingServletRequestParameterException ex) {
      log.error("{},{}",e.getClass(), e.getMessage());
      return ResponseEntity.badRequest().body(ErrorResponse.fromMissingRequestParameterException(ex));
    }
    if (e instanceof MissingRequestHeaderException ex) {
      log.error("{},{}",e.getClass(), e.getMessage());
      return ResponseEntity.badRequest().body(ErrorResponse.fromMissingRequestHeaderException(ex));
    }
    if (e instanceof HttpMessageNotReadableException ex) {
      log.error("{},{}",e.getClass(), e.getMessage());
      return ResponseEntity.badRequest().body(ErrorResponse.fromHttpMessageNotReadableException(ex));
    }
    log.error("{},{}",e.getClass(), e.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.name(),
            "예상치 못한 에러 발생",
            Map.of(),
            e.getClass().getSimpleName(),
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        ));
  }

}
