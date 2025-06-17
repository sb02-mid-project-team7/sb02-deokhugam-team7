package com.sprint.deokhugamteam7.domain.book.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.deokhugamteam7.domain.book.dto.BookActivity;
import com.sprint.deokhugamteam7.domain.book.dto.BookDto;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BookDtoEntityTest {
  private Book book;
  private UUID bookId;
  private LocalDate publishedDate;

  @BeforeEach
  void setUp() {
    // 엔티티 필드 셋업
    publishedDate = LocalDate.of(2025, 6, 10);

    // Book 엔티티 생성 (빌더 또는 생성 메서드 활용)
    book = Book.create("데미안", "헤르만 허세","을유문화사",publishedDate)
        .description("한 청년의 자아를 찾아가는 이야기")
        .isbn("9788990982575")
        .thumbnailUrl("https://example.com/thumbnail.jpg")
        .build();
  }

  @Test
  @DisplayName("BookActivity에 리뷰가 있을 때 rating 계산이 올바르게 된다")
  void from_withReviews_calculatesRating() {
    // given: reviewCount=4, totalRating=14 → rating=14/4=3.5
    BookActivity activity = new BookActivity(bookId, 4L, 14);

    // when
    BookDto dto = BookDto.from(book, activity);

    // then
    assertThat(dto.title()).isEqualTo("데미안");
    assertThat(dto.author()).isEqualTo("헤르만 허세");
    assertThat(dto.description()).isEqualTo("한 청년의 자아를 찾아가는 이야기");
    assertThat(dto.publisher()).isEqualTo("을유문화사");
    assertThat(dto.publishedDate()).isEqualTo(publishedDate);
    assertThat(dto.isbn()).isEqualTo("9788990982575");
    assertThat(dto.thumbnailUrl()).isEqualTo("https://example.com/thumbnail.jpg");

    // activity 기반 필드
    assertThat(dto.reviewCount()).isEqualTo(4L);
    assertThat(dto.rating()).isEqualTo(3.5);
  }

  @Test
  @DisplayName("BookActivity에 리뷰가 없을 때 rating은 0이 된다")
  void from_withoutReviews_setsRatingZero() {
    // given: reviewCount=0, totalRating=0
    BookActivity activity = new BookActivity(bookId, 0L, 0);

    // when
    BookDto dto = BookDto.from(book, activity);

    // then
    assertThat(dto.reviewCount()).isZero();
    assertThat(dto.rating()).isZero();
  }
}
