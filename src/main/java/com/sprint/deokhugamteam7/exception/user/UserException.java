package com.sprint.deokhugamteam7.exception.user;

import com.sprint.deokhugamteam7.exception.DeokhugamException;
import com.sprint.deokhugamteam7.exception.ErrorCode;

public class UserException extends DeokhugamException {

  public UserException(ErrorCode errorCode) {
    super(errorCode);
  }
}
