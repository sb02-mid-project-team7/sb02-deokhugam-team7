package com.sprint.deokhugamteam7.exception;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
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
    ErrorCode errorCode = ErrorCode.METHOD_ARGUMENT_NOT_VALID;
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
        errorCode.name(),
        errorCode.getMessage(),
        details,
        e.getClass().getSimpleName(),
        e.getStatusCode().value()
    );
  }

  public static ErrorResponse fromArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e) {
    ErrorCode errorCode = ErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH;

    Map<String, String> details = Map.of(
        "parameter", e.getName(),
        "invalidValue", e.getValue() != null ? e.getValue().toString() : "null",
        "requiredType", e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown"
    );

    return new ErrorResponse(
        LocalDateTime.now(),
        errorCode.name(),
        errorCode.getMessage(),
        details,
        e.getClass().getSimpleName(),
        errorCode.getHttpStatus().value()
    );
  }

  public static ErrorResponse fromMissingPathVariableException(
      MissingPathVariableException e) {
    ErrorCode errorCode = ErrorCode.MISSING_PATH_VARIABLE;

    Map<String, String> details = Map.of(
        "variableName", e.getVariableName()
    );

    return new ErrorResponse(
        LocalDateTime.now(),
        errorCode.name(),
        errorCode.getMessage(),
        details,
        e.getClass().getSimpleName(),
        errorCode.getHttpStatus().value()
    );
  }

  public static ErrorResponse fromMissingRequestParameterException(
      MissingServletRequestParameterException e) {
    ErrorCode errorCode = ErrorCode.MISSING_REQUEST_PARAMETER;

    Map<String, String> details = Map.of(
        "parameterName", e.getParameterName(),
        "parameterType", e.getParameterType()
    );

    return new ErrorResponse(
        LocalDateTime.now(),
        errorCode.name(),
        errorCode.getMessage(),
        details,
        e.getClass().getSimpleName(),
        errorCode.getHttpStatus().value()
    );
  }

  public static ErrorResponse fromMissingRequestHeaderException(
      MissingRequestHeaderException e) {
    ErrorCode errorCode = ErrorCode.MISSING_REQUEST_HEADER;

    Map<String, String> details = Map.of(
        "headerName", e.getHeaderName()
    );

    return new ErrorResponse(
        LocalDateTime.now(),
        errorCode.name(),
        errorCode.getMessage(),
        details,
        e.getClass().getSimpleName(),
        errorCode.getHttpStatus().value()
    );
  }

  public static ErrorResponse fromHttpMessageNotReadableException(
      HttpMessageNotReadableException e) {
    ErrorCode errorCode = ErrorCode.HTTP_MESSAGE_NOT_READABLE;

    return new ErrorResponse(
        LocalDateTime.now(),
        errorCode.name(),
        errorCode.getMessage(),
        Map.of(),
        e.getClass().getSimpleName(),
        errorCode.getHttpStatus().value()
    );
  }
}
