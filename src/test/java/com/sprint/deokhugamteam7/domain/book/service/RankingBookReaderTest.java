package com.sprint.deokhugamteam7.domain.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.domain.book.batch.step.RankingBookReader;
import com.sprint.deokhugamteam7.domain.book.dto.BookActivity;
import com.sprint.deokhugamteam7.domain.book.repository.RankingBookRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RankingBookReaderTest {

  @Mock
  private RankingBookRepository rankingBookRepository;

  private final String startStr = "2025-06-10T00:00:00";
  private final String endStr   = "2025-06-12T00:00:00";

  private LocalDateTime start;
  private LocalDateTime end;

  @BeforeEach
  void init() {
    start = LocalDateTime.parse(startStr);
    end   = LocalDateTime.parse(endStr);
  }

  @Test
  @DisplayName("주어진 start/end 기간으로 저장된 BookActivity를 순차적으로 읽는다")
  void read_returnsAllItems_thenNull() throws Exception {
    // given
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    BookActivity act1 = new BookActivity(id1, 2L, 5);
    BookActivity act2 = new BookActivity(id2, 3L, 6);

    when(rankingBookRepository.findReviewActivitySummary(start, end))
        .thenReturn(List.of(act1, act2));

    RankingBookReader reader =
        new RankingBookReader(rankingBookRepository, startStr, endStr);

    // when / then
    assertThat(reader.read()).isEqualTo(act1);
    assertThat(reader.read()).isEqualTo(act2);
    assertThat(reader.read()).isNull();  // 더 이상 읽을 데이터가 없으면 null

    // repository 가 딱 한 번만 호출됐는지 확인
    verify(rankingBookRepository, times(1))
        .findReviewActivitySummary(start, end);
  }

  @Test
  @DisplayName("start/end가 빈 문자열이면 null로 파싱되어 전체를 조회하고 null을 반환한다")
  void read_withBlankParameters_returnsNullImmediately() throws Exception {
    // given: 빈 문자열로 생성
    when(rankingBookRepository.findReviewActivitySummary(null, null))
        .thenReturn(List.of());  // 빈 리스트

    RankingBookReader reader =
        new RankingBookReader(rankingBookRepository, "", "");

    // when / then
    assertThat(reader.read()).isNull();
    verify(rankingBookRepository, times(1))
        .findReviewActivitySummary(null, null);
  }
}