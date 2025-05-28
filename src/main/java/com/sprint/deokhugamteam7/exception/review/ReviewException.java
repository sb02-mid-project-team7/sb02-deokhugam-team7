package com.sprint.deokhugamteam7.exception.review;

import com.sprint.deokhugamteam7.exception.DeokhugamException;
import com.sprint.deokhugamteam7.exception.ErrorCode;

public class ReviewException extends DeokhugamException {

  public ReviewException(ErrorCode errorCode) {
    super(errorCode);
  }
}
