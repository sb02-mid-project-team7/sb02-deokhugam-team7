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
    responseCode = "404",
    description = "리뷰 정보 없음",
    content = @Content(
        schema = @Schema(implementation = ErrorResponse.class),
        examples = @ExampleObject(
            value = """
                {
                  "timestamp": "2025-06-09T15:22:24.5323466",
                  "code": "REVIEW_NOT_FOUND",
                  "message": "리뷰를 찾을 수 없습니다.",
                  "details": {},
                  "exceptionType": "ReviewException",
                  "status": 404
                }
                """
        )
    )
)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ReviewNotFoundResponse {

}
