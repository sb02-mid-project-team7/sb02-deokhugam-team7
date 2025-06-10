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
    responseCode = "500",
    description = "서버 내부 오류",
    content = @Content(
        schema = @Schema(implementation = ErrorResponse.class),
        examples = @ExampleObject(
            value = """
                {
                  "timestamp": "2025-06-09T15:22:24.5323466",
                  "code": "INTERNAL_SERVER_ERROR",
                  "message": "서버 내부 오류",
                  "details": {},
                  "exceptionType": "Exception",
                  "status": 500
                }
                """
        )
    )
)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface InternalServerErrorResponse {

}
