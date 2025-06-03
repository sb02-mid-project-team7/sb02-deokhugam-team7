package com.sprint.deokhugamteam7.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionAdvice {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleDeokhugamException(DeokhugamException e) {
        HttpStatus status = e.getErrorCode().getHttpStatus();
        return ResponseEntity
            .status(status)
            .body(ErrorResponse.fromDeokhugamException(e));
    }

}
