package com.sprint.deokhugamteam7;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.deokhugamteam7.domain.book.dto.NaverBookDto;
import com.sprint.deokhugamteam7.domain.book.service.APIService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class APIIntegrationTest {

  @Autowired
  private APIService apiService;

  @Test
  void testSearchBook() throws Exception {
    // given
    String isbn = "9788994506074";
    // when & then
    NaverBookDto naverBookDto = apiService.searchBooks(isbn);

    assertThat(naverBookDto).isNotNull();
    assertThat(naverBookDto.isbn()).isEqualTo(isbn);
  }


}
