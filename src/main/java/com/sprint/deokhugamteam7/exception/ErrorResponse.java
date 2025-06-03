package com.sprint.deokhugamteam7.exception;

import java.time.LocalDateTime;
import java.util.Map;

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
}
