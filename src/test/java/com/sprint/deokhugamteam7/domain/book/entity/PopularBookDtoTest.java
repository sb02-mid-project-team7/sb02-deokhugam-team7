package com.sprint.deokhugamteam7.domain.book.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.deokhugamteam7.domain.book.dto.FindPopularBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.PopularBookDto;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PopularBookDto 단위 테스트")
class PopularBookDtoTest {

  private UUID id;
  private UUID bookId;
  private LocalDateTime createdAt;
  private String title;
  private String author;
  private String thumbnailUrl;
  private String period;
  private double score;
  private long reviewCount;
  private double rating;
  private int expectedRank;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    bookId = UUID.randomUUID();
    createdAt = LocalDateTime.of(2025, 6, 10, 13, 0, 0);
    title = "데미안";
    author = "헤르만 허세";
    thumbnailUrl = "https://example.com/thumbnail.jpg";
    period = "DAILY";
    score = 6.9;
    reviewCount = 12L;
    rating = 3.5;
    expectedRank = 1;
  }

  @Test
  @DisplayName("from() 메서드는 FindPopularBookDto와 rank를 올바르게 매핑한다")
  void from_mapsAllFieldsCorrectly() {
    // given
    FindPopularBookDto bookDto = new FindPopularBookDto(
        id,
        bookId,
        createdAt,
        title,
        author,
        thumbnailUrl,
        period,
        score,
        reviewCount,
        rating
    );

    // when
    PopularBookDto dto = PopularBookDto.from(bookDto, expectedRank);

    // then
    assertThat(dto.id()).isEqualTo(id);
    assertThat(dto.bookId()).isEqualTo(bookId);
    assertThat(dto.createdAt()).isEqualTo(createdAt);
    assertThat(dto.title()).isEqualTo(title);
    assertThat(dto.author()).isEqualTo(author);
    assertThat(dto.thumbnailUrl()).isEqualTo(thumbnailUrl);
    assertThat(dto.period()).isEqualTo(period);
    assertThat(dto.score()).isEqualTo(score);
    assertThat(dto.reviewCount()).isEqualTo(reviewCount);
    assertThat(dto.rating()).isEqualTo(rating);
    assertThat(dto.rank()).isEqualTo(expectedRank);
  }
}