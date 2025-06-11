package com.sprint.deokhugamteam7.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class DeokhugamException extends RuntimeException {

  private final LocalDateTime timestamp;
  private final ErrorCode errorCode;
  private final Map<String, String> details;

  public DeokhugamException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.timestamp = LocalDateTime.now();
    this.errorCode = errorCode;
    this.details = Map.of();
  }

  public DeokhugamException(ErrorCode errorCode, Map<String, String> details) {
    super(errorCode.getMessage());
    this.timestamp = LocalDateTime.now();
    this.errorCode = errorCode;
    this.details = details;
  }
}
