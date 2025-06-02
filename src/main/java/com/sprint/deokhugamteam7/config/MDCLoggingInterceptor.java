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
      // UUID
      String requestId = request.getHeader("X-Request-ID");
      if (requestId == null) {
        requestId = UUID.randomUUID().toString();
      }

      MDC.put("requestId", requestId);

      String requestIp = request.getRequestURI();

      //품질 관리 합시다.
      MDC.put("ip", requestIp);

      response.setHeader(HEADER, requestId);
      filterChain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }
}
