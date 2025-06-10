package com.sprint.deokhugamteam7.swagger;


import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.http.MediaType;

@Parameter(
    description = "도서 정보",
    required = true,
    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface BookRequestParameter {

}
