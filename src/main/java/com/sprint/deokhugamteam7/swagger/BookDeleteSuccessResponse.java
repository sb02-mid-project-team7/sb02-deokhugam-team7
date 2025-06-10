package com.sprint.deokhugamteam7.swagger;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ApiResponse(
    responseCode = "204",
    description = "Book이 성공적으로 삭제됨")
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface BookDeleteSuccessResponse {

}
