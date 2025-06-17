package com.sprint.deokhugamteam7.config;

import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class UserAccessFilter extends OncePerRequestFilter {

  private static final String HEADER_NAME = "Deokhugam-Request-User-ID";

  private final UserRepository userRepository;

  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  private static final List<String> WHITE_LIST = List.of(
      "/", "/index.html", "/favicon.ico","/batch_dashboard.html",
      "/static/**", "/assets/**", "/css/**", "/js/**", "/images/**",
      "/api/users", "/api/users/login", "/api/users/power","/api/batch/**",
      "/api/reviews/popular", "/api/books/popular", "/actuator/**"
  );

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String uri = request.getRequestURI();
    return WHITE_LIST.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
  }


  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String headerUserId = request.getHeader(HEADER_NAME);
    Object sessionUserId = request.getSession().getAttribute("userId");

    if (headerUserId == null || headerUserId.isBlank() || sessionUserId == null) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "사용자 인증 정보가 없습니다.");
      return;
    }

    try {
      UUID headerId = UUID.fromString(headerUserId);
      UUID sessionId = UUID.fromString(sessionUserId.toString());

      if (!headerId.equals(sessionId)) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "세션 정보와 헤더 정보가 일치하지 않습니다.");
        return;
      }

      if (!userRepository.existsById(headerId)) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "존재하지 않는 사용자입니다.");
        return;
      }

      filterChain.doFilter(request, response);
    } catch (IllegalArgumentException e) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "잘못된 사용자 ID 형식입니다.");
    }
  }
}