package com.sprint.deokhugamteam7.exception;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public class DeokhugamException extends RuntimeException {

  private final LocalDate timestamp;
  private final ErrorCode errorCode;

  public DeokhugamException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.timestamp = LocalDate.now();
    this.errorCode = errorCode;
  }
}
