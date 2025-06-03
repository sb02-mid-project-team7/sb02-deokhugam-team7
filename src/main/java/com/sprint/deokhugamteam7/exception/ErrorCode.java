package com.sprint.deokhugamteam7.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "Internal Server Error"),
  NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "?", "알림을 찾을 수 없습니다."),
  NOTIFICATION_NOT_OWNED(HttpStatus.FORBIDDEN, "?", "본인의 알람만 조회/수정할 수 있습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
