package com.sprint.deokhugamteam7.exception.book;

import com.sprint.deokhugamteam7.exception.DeokhugamException;
import com.sprint.deokhugamteam7.exception.ErrorCode;

public class BookException extends DeokhugamException {

  public BookException(ErrorCode errorCode) {
    super(errorCode);
  }
}
