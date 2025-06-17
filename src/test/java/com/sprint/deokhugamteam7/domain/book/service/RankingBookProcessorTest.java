package com.sprint.deokhugamteam7.domain.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.batch.step.RankingBookProcessor;
import com.sprint.deokhugamteam7.domain.book.dto.BookActivity;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.entity.RankingBook;
import com.sprint.deokhugamteam7.domain.book.repository.BookRepository;
import com.sprint.deokhugamteam7.exception.book.BookException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RankingBookProcessorTest {

  @Mock
  private BookRepository bookRepository;

  private RankingBookProcessor processor;

  private UUID bookId;

  @BeforeEach
  void setUp() {
    // 'DAILY' 는 toUpperCase 되어 Period.DAILY 로 변환됩니다
    processor = new RankingBookProcessor(bookRepository, "DAILY");
    bookId = UUID.randomUUID();
  }

  @Test
  @DisplayName("리뷰가 있는 경우 rating·score·reviewCount를 계산하여 새로운 RankingBook을 반환한다")
  void process_withReviews_calculatesCorrectRankingBook() {
    // given
    // BookRepository.findById 에 대해 실제 Book 엔티티를 반환하도록 스텁
    Book book = Book.create("a", "b", "c", LocalDate.now()).build();
    when(bookRepository.findById(bookId))
        .thenReturn(Optional.of(book));

    // reviewCount=5, totalRating=20 => rating=4.0, score=5*0.4 + 4.0*0.6 = 2.0 + 2.4 = 4.4
    BookActivity activity = new BookActivity(bookId, 5L, 20);

    // when
    RankingBook result = processor.process(activity);

    // then
    assertThat(result.getBook()).isSameAs(book);
    assertThat(result.getPeriod()).isEqualTo(Period.DAILY);
    assertThat(result.getReviewCount()).isEqualTo(5L);
    assertThat(result.getRating()).isEqualTo(4.0);
    assertThat(result.getScore()).isEqualTo(4.4);
    // 새로 만든 RankingBook 은 rank 기본값 0
    assertThat(result.getRank()).isZero();
  }

  @Test
  @DisplayName("리뷰가 없는 경우 rating=0·score=0·reviewCount=0 인 RankingBook을 반환한다")
  void process_withoutReviews_returnsDefaultRankingBook() {
    // given
    Book book = Book.create("a", "b", "c", LocalDate.now()).build();
    when(bookRepository.findById(bookId))
        .thenReturn(Optional.of(book));

    BookActivity activity = new BookActivity(bookId, 0L, 0);

    // when
    RankingBook result = processor.process(activity);

    // then
    assertThat(result.getBook()).isSameAs(book);
    assertThat(result.getPeriod()).isEqualTo(Period.DAILY);
    assertThat(result.getReviewCount()).isZero();
    assertThat(result.getRating()).isZero();
    assertThat(result.getScore()).isZero();
    assertThat(result.getRank()).isZero();
  }

  @Test
  @DisplayName("존재하지 않는 도서 ID가 들어오면 BookException을 던진다")
  void process_whenBookNotFound_throwsBookException() {
    // given
    when(bookRepository.findById(bookId))
        .thenReturn(Optional.empty());

    BookActivity activity = new BookActivity(bookId, 1L, 1);

    // when / then
    assertThrows(BookException.class, () -> processor.process(activity));
  }
}
