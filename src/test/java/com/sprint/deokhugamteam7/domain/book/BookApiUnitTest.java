package com.sprint.deokhugamteam7.domain.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.deokhugamteam7.domain.book.dto.NaverBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.response.Item;
import com.sprint.deokhugamteam7.domain.book.dto.response.NaverBookResponse;
import com.sprint.deokhugamteam7.domain.book.service.APIService;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class BookApiUnitTest {
  @Mock
  private RestTemplate restTemplate;

  @Spy
  private ObjectMapper objectMapper;

  @InjectMocks
  private APIService apiService;

  @Test
  void searchBooks_returnsDto() throws Exception {
    // given
    String isbn = "1";
    ResponseEntity<String> fake = new ResponseEntity<>("{}", HttpStatus.OK);
    when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenReturn(fake);
    Item item = new Item("테스트책", "링크", "이미지 링크", "저자",
        1234, "출판사", 1, "설명", "20250101");
    NaverBookResponse mockResp = new NaverBookResponse(null, 1, 0, 1, List.of(item));

    doReturn(mockResp)
        .when(objectMapper)
        .readValue(anyString(), eq(NaverBookResponse.class));

    // when

    NaverBookDto dto = apiService.searchBooks(isbn);

    // then
    assertThat(dto).isNotNull();
    assertThat(dto.isbn()).isEqualTo(isbn);
  }
}
