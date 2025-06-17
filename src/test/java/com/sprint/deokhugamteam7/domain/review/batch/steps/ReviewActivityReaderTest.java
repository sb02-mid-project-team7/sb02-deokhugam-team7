package com.sprint.deokhugamteam7.domain.review.batch.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.domain.review.batch.step.ReviewActivityReader;
import com.sprint.deokhugamteam7.domain.review.dto.ReviewActivity;
import com.sprint.deokhugamteam7.domain.review.repository.custom.ReviewRepositoryCustom;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class ReviewActivityReaderTest {

  @Mock
  private ReviewRepositoryCustom reviewRepositoryCustom;

  private String startStr;
  private String endStr;
  private LocalDateTime start;
  private LocalDateTime end;

  @BeforeEach
  void setUp() {
    startStr = "2025-06-10T00:00:00";
    endStr   = "2025-06-12T00:00:00";
    start    = LocalDateTime.parse(startStr);
    end      = LocalDateTime.parse(endStr);
  }

  @Test
  @DisplayName("주어진 start/end 기간으로 ReviewActivity를 순차적으로 읽는다")
  void read_returnsAllItems_thenNull() {
    ReviewActivity act1 = mock(ReviewActivity.class);
    ReviewActivity act2 = mock(ReviewActivity.class);

    when(reviewRepositoryCustom.findReviewActivitySummary(start, end))
        .thenReturn(List.of(act1, act2));

    ReviewActivityReader reader =
        new ReviewActivityReader(reviewRepositoryCustom, startStr, endStr);

    assertThat(reader.read()).isSameAs(act1);
    assertThat(reader.read()).isSameAs(act2);
    assertNull(reader.read());

    verify(reviewRepositoryCustom, times(1))
        .findReviewActivitySummary(start, end);
  }

  @Test
  @DisplayName("start/end가 빈 문자열이면 전체 조회 후 바로 null을 반환한다")
  void read_withBlankParameters_returnsNull() {
    when(reviewRepositoryCustom.findReviewActivitySummary(null, null))
        .thenReturn(List.of());

    ReviewActivityReader reader =
        new ReviewActivityReader(reviewRepositoryCustom, "", "");

    assertNull(reader.read());

    verify(reviewRepositoryCustom, times(1))
        .findReviewActivitySummary(null, null);
  }
}

