package com.sprint.deokhugamteam7.domain.book.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.deokhugamteam7.domain.book.dto.NaverBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.response.NaverBookResponse;
import com.sprint.deokhugamteam7.exception.DeokhugamException;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
@Slf4j
public class APIService {

  private final ObjectMapper objectMapper;

  private final RestTemplate restTemplate;

  @Value("${naver.client-id}")
  private String clientId;

  @Value("${naver.client-secret}")
  private String clientSecret;

  private static final String NAVER_BOOK_API_URL = "https://openapi.naver.com/v1/search/book.json";

  public NaverBookDto searchBooks(String query) {
//    log.info("API 호출: query {}", query);
    try {
      UriComponents uri = UriComponentsBuilder.fromUriString(NAVER_BOOK_API_URL)
          .queryParam("query", query).build(true);
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.set("X-Naver-Client-Id", clientId);
      httpHeaders.set("X-Naver-Client-Secret", clientSecret);
      httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
      HttpEntity<?> requestEntity = new HttpEntity<>(httpHeaders);

      ResponseEntity<String> responseEntity = restTemplate.exchange(
          uri.toUri(),
          HttpMethod.GET,
          requestEntity,
          String.class
      );
      NaverBookResponse response = objectMapper.readValue(responseEntity.getBody(),
          NaverBookResponse.class);
      NaverBookDto naverBookDto = NaverBookDto.from(response.items().get(0));

//      log.info("API 호출 완료 : title {}, isbn {}", naverBookDto.title(), naverBookDto.isbn());
      return naverBookDto;
    } catch (Exception e) {
//      log.error("네이버 책 검색 API 호출 실패", e);
      throw new DeokhugamException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
  }
}
