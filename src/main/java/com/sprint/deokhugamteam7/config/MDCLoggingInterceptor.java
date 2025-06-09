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
      // 사용자 ID 설정
      Object userId = request.getSession().getAttribute("userId");
      String requestId = userId != null ? userId.toString() : "non-member";
      MDC.put("requestId", requestId);
      response.setHeader(HEADER, requestId);

      // 클라이언트 IP 설정
      String requestIp = request.getHeader("X-Forwarded-For");
      if (requestIp == null || requestIp.isBlank()) {
        requestIp = request.getRemoteAddr();
      }
      MDC.put("requestIp", requestIp);

      filterChain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }
}
