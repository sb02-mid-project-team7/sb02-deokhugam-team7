package com.sprint.deokhugamteam7.domain.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.deokhugamteam7.config.QueryDslConfig;
import com.sprint.deokhugamteam7.config.TestAuditingConfig;
import com.sprint.deokhugamteam7.domain.book.dto.BookActivity;
import com.sprint.deokhugamteam7.domain.book.dto.condition.BookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.condition.PopularBookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponseBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponsePopularBookDto;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.repository.custom.RankingBookRepositoryImpl;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import({QueryDslConfig.class, TestAuditingConfig.class})
public class RankingBookRepositoryTest {

  @Autowired
  private TestEntityManager em;

  @Autowired
  private RankingBookRepositoryImpl rankingBookRepository;

  private Book book;

  @BeforeEach
  void setUp() {
    LocalDate date = LocalDate.of(2025, 6, 10);
    book = Book.create(
        "제목", "작가", "퍼블리셔", date
    ).build();
    em.persist(book);
    em.flush();
    em.clear();
  }

  @Test
  void findAllByKeyword_default_success() {
    // given
    BookCondition cond = new BookCondition();
    cond.setKeyword("제목");
    // when
    CursorPageResponseBookDto result1 = rankingBookRepository.findAllByKeyword(cond);

    cond.setDirection("asc");
    CursorPageResponseBookDto result2 = rankingBookRepository.findAllByKeyword(cond);

    cond.setCursor("title");
    CursorPageResponseBookDto result3 = rankingBookRepository.findAllByKeyword(cond);
    LocalDateTime newDate = LocalDateTime.of(2025, 6, 10, 0, 0, 0);
    cond.setAfter(newDate);
    CursorPageResponseBookDto result4 = rankingBookRepository.findAllByKeyword(cond);

    // then
    assertAll(
        () -> assertThat(result1.content()).hasSize(1),
        () -> assertThat(result2.content()).hasSize(1),
        () -> assertThat(result3.content()).hasSize(1),
        () -> assertThat(result4.content()).hasSize(1),
        () -> assertThat(result1.content().get(0).title()).isEqualTo("제목"),
        () -> assertThat(result2.content().get(0).title()).isEqualTo("제목"),
        () -> assertThat(result3.content().get(0).title()).isEqualTo("제목"),
        () -> assertThat(result4.content().get(0).title()).isEqualTo("제목")
    );
  }

  @Test
  void findAllByKeyword_publishedDate_success() {
    // given
    BookCondition cond = new BookCondition();
    cond.setOrderBy("publishedDate");
    // when
    CursorPageResponseBookDto result1 = rankingBookRepository.findAllByKeyword(cond);
    cond.setDirection("asc");
    CursorPageResponseBookDto result2 = rankingBookRepository.findAllByKeyword(cond);
    cond.setCursor("2025-06-10");
    CursorPageResponseBookDto result3 = rankingBookRepository.findAllByKeyword(cond);

    // then
    assertAll(
        () -> assertThat(result1.content()).hasSize(1),
        () -> assertThat(result2.content()).hasSize(1),
        () -> assertThat(result3.content()).hasSize(0),
        () -> assertThat(result1.content().get(0).title()).isEqualTo("제목"),
        () -> assertThat(result2.content().get(0).title()).isEqualTo("제목")
    );
  }

  @Test
  void findAllByKeyword_rating_success() {
    // given
    BookCondition cond = new BookCondition();
    cond.setOrderBy("rating");
    cond.setLimit(1);
    // when
    CursorPageResponseBookDto page1 = rankingBookRepository.findAllByKeyword(cond);

    cond.setDirection("asc");
    cond.setCursor(null);
    CursorPageResponseBookDto page2 = rankingBookRepository.findAllByKeyword(cond);

    // then
    assertAll(
        () -> assertThat(page1.content()).hasSize(1),
        () -> assertThat(page2.content()).hasSize(1),
        () -> assertThat(page1.content().get(0).title()).isEqualTo("제목"),
        () -> assertThat(page2.content().get(0).title()).isEqualTo("제목")
    );
  }

  @Test
  void findAllByKeyword_reviewCount_success() {
    // given
    BookCondition cond = new BookCondition();
    cond.setOrderBy("reviewCount");
    cond.setLimit(1);

    // when
    CursorPageResponseBookDto page1 = rankingBookRepository.findAllByKeyword(cond);

    cond.setDirection("asc");
    cond.setCursor(null);
    CursorPageResponseBookDto page2 = rankingBookRepository.findAllByKeyword(cond);

    // then
    assertAll(
        () -> assertThat(page1.content()).hasSize(1),
        () -> assertThat(page2.content()).hasSize(1),
        () -> assertThat(page1.content().get(0).title()).isEqualTo("제목"),
        () -> assertThat(page2.content().get(0).title()).isEqualTo("제목")
    );
  }

  @Test
  void findPopularBooks_default_Success() {
    // given
    PopularBookCondition cond = new PopularBookCondition();
    cond.setLimit(1);
    // when
    CursorPageResponsePopularBookDto result =
        rankingBookRepository.findPopularBooks(cond);
    // then
    assertThat(result.content()).isEmpty();
    assertThat(result.nextCursor()).isNull();
  }

  @Test
  void findReviewActivitySummary_withoutFilters() {
    // given
    User mockUser = User.create("a@a.com", "a", "a");
    em.persist(mockUser);

    Review r1 = Review.create(book, mockUser, "첫 리뷰", 4);
    Review r2 = Review.create(book, mockUser, "두 번째 리뷰", 3);

    em.persist(r1);
    em.persist(r2);
    em.flush();
    em.clear();

    // when
    List<BookActivity> summary = rankingBookRepository.findReviewActivitySummary(null, null);

    // then
    assertThat(summary).hasSize(1);
    BookActivity act = summary.get(0);
    assertThat(act.bookId()).isEqualTo(book.getId());
    assertThat(act.reviewCount()).isEqualTo(2L);
    assertThat(act.totalRating()).isEqualTo(7);
  }

  @Test
  void findReviewActivitySummary_withDateFilters() {
    // given
    User mockUser = User.create("a@a.com", "a", "a");
    em.persist(mockUser);

    Review inside = Review.create(book, mockUser, "첫 리뷰", 5);

    Review outside = Review.create(book, mockUser, "두 번째 리뷰", 2);

    em.persist(inside);
    em.persist(outside);
    em.flush();
    em.clear();

    // when
    LocalDateTime start = LocalDateTime.of(2025, 6, 17, 0, 0, 0);
    LocalDateTime end   = LocalDateTime.of(2025, 6, 18, 0, 0, 0);
    List<BookActivity> summary = rankingBookRepository.findReviewActivitySummary(start, end);

    // then
    assertThat(summary).hasSize(1);
    BookActivity act = summary.get(0);
    assertThat(act.reviewCount()).isEqualTo(0L);
    assertThat(act.totalRating()).isEqualTo(0);
  }
}
