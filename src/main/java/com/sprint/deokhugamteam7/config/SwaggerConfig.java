package com.sprint.deokhugamteam7.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("덕후감 API 문서")
            .description("7팀 프로젝트 덕후감의 Swagger API 문서입니다.")
        );
  }

}
