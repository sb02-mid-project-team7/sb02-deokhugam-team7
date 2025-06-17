package com.sprint.deokhugamteam7.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Value("${deokhugam.storage.local.root-path}")
  private String uploadPath;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/images/**")
        .addResourceLocations("file:" + uploadPath + "/");
    // 배치 대시보드 호출용 매핑
    registry.addResourceHandler("/batch-dashboard.html")
        .addResourceLocations("classpath:/static/batch_dashboard.html");
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    // /batch-dashboard 엔드포인트를 static 배치 대시보드로 포워드
    registry.addViewController("/batch-dashboard")
        .setViewName("forward:/batch_dashboard.html");
  }
}
