package com.sprint.deokhugamteam7.swagger;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ApiResponse(
    responseCode = "200",
    description = "Book 목록이 성공적으로 조회됨")
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface BookFindSuccessResponse {

}
