package com.sprint.deokhugamteam7.exception.comment;

import com.sprint.deokhugamteam7.exception.DeokhugamException;
import com.sprint.deokhugamteam7.exception.ErrorCode;

public class CommentException extends DeokhugamException {

  public CommentException(ErrorCode errorCode) {
    super(errorCode);
  }
}
