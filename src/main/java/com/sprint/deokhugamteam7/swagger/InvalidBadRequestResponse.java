package com.sprint.deokhugamteam7.swagger;

import com.sprint.deokhugamteam7.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ApiResponse(
    responseCode = "400",
    description = "잘못된 요청 (입력값 검증 실패)",
    content = @Content(
        schema = @Schema(implementation = ErrorResponse.class),
        examples = @ExampleObject(
            value = """
                      {
                        "timestamp": "2025-06-09T15:22:24.5323466",
                        "code": "METHOD_ARGUMENT_NOT_VALID",
                        "message": "@Valid 유효성 검사에 실패했습니다.",
                        "details": {},
                        "exceptionType": "MethodArgumentNotValidException",
                        "status": 400
                      }
                      """
        )
    )
)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface InvalidBadRequestResponse {

}
