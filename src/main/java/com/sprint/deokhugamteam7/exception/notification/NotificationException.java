package com.sprint.deokhugamteam7.exception.notification;

import com.sprint.deokhugamteam7.exception.DeokhugamException;
import com.sprint.deokhugamteam7.exception.ErrorCode;

public class NotificationException extends DeokhugamException {

  public NotificationException(ErrorCode errorCode) {
    super(errorCode);
  }
}
