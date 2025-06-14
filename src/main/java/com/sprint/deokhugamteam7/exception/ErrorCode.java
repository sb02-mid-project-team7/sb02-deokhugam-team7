package com.sprint.deokhugamteam7.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  //공통 오류
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "서버 내부 오류"),


  // 사용자 요청 오류
  INTERNAL_BAD_REQUEST(HttpStatus.BAD_REQUEST,"?","잘못된 요청입니다."),
  METHOD_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "?", "@Valid 유효성 검사에 실패했습니다."), // 완료
  METHOD_ARGUMENT_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "?", "요청 파라미터 타입이 잘못되었습니다."), // 완료
  MISSING_PATH_VARIABLE(HttpStatus.BAD_REQUEST, "?", "PathVariable이 누락되었습니다."),
  MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "?", "Request parameter가 누락되었습니다."),
  MISSING_REQUEST_HEADER(HttpStatus.BAD_REQUEST, "?", "Request header가 누락되었습니다."),
  HTTP_MESSAGE_NOT_READABLE(HttpStatus.BAD_REQUEST, "?", "HttpMessage를 읽을 수 없습니다."),

  //알림 오류
  NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "?", "알림을 찾을 수 없습니다."),
  NOTIFICATION_NOT_OWNED(HttpStatus.FORBIDDEN, "?", "본인의 알람만 조회/수정할 수 있습니다."),

  //사용자 오류
  DUPLICATE_EMAIL(HttpStatus.CONFLICT, "?", "중복된 이메일입니다."),
  LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "?", "로그인에 실패했습니다(이메일 또는 비밀번호 불일치)"),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "?", "사용자를 찾을 수 없습니다."),
  USER_FORBIDDEN(HttpStatus.FORBIDDEN, "?", "사용자의 권한이 없습니다."),


  //도서 오류
  BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "?", "도서를 찾을 수 없습니다."),
  DUPLICATE_ISBN(HttpStatus.CONFLICT,"?","동일한 ISBN 존재합니다."),

  //리뷰 오류
  REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "?", "리뷰를 찾을 수 없습니다."),
  REVIEW_NOT_OWNED(HttpStatus.FORBIDDEN, "?", "자신의 리뷰만 수정/삭제할 수 있습니다."),
  REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "?", "이미 해당 도서에 대한 리뷰가 존재합니다."),

  //댓글 오류
  COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "?", "댓글을 찾을 수 없습니다."),
  COMMENT_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "?", "해당 댓글을 수정할 권한이 없습니다."),
  COMMENT_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "?", "해당 댓글을 삭제할 권한이 없습니다.")
  ;

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
