package com.sprint.deokhugamteam7.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  INTERNAL_SERVER_ERROR(500, "S001", "Internal Server Error");

  private final int httpStatus;
  private final String code;
  private final String message;
}
