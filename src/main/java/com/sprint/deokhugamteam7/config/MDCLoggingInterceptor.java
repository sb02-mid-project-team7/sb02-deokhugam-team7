package com.sprint.deokhugamteam7.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class MDCLoggingInterceptor extends OncePerRequestFilter {

  private static final String HEADER = "Deokhugam-Request-User-ID";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      // 세션에서 사용자 ID 가져오기
      Object userId = request.getSession().getAttribute("userId");

      if (userId != null) {
        response.setHeader(HEADER, userId.toString());
        MDC.put("userId", userId.toString());
      } else {
        // 비로그인 사용자 처리
        response.setHeader(HEADER, "non-member");
        MDC.put("userId", "non-member");
      }

      String requestIp = request.getRequestURI();

      //품질 관리 합시다.
      MDC.put("ip", requestIp);

      filterChain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }
}
