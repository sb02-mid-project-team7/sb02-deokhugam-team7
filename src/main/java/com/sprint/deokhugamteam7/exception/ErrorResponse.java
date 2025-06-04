package com.sprint.deokhugamteam7.exception;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

public record ErrorResponse(
    LocalDateTime timestamp,
    String code,
    String message,
    Map<String, String> details,
    String exceptionType,
    int status
) {
    public static ErrorResponse fromDeokhugamException(DeokhugamException e) {
        return new ErrorResponse(
            e.getTimestamp(),
            e.getErrorCode().name(),
            e.getErrorCode().getMessage(),
            e.getDetails(),
            e.getClass().getSimpleName(),
            e.getErrorCode().getHttpStatus().value()
        );
    }

  public static ErrorResponse fromMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {

    Map<String, String> details = e.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            fieldError -> {
              String msg = fieldError.getDefaultMessage();
              //TODO 최적화 대상
              return (msg != null && !msg.isBlank()) ? msg : "Invalid value";
            },
            (existing, replacement) -> existing
        ));

    return new ErrorResponse(
        LocalDateTime.now(),
        e.getStatusCode().toString(),
        e.getMessage(),
        details,
        e.getClass().getSimpleName(),
        e.getStatusCode().value()
    );
  }

  public static ErrorResponse fromArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e,
      HttpStatus status
  ) {
    return new ErrorResponse(
        LocalDateTime.now(),
        e.getErrorCode(),
        e.getMessage(),
        Map.of(),
        e.getClass().getSimpleName(),
        status.value()
    );
  }

}
