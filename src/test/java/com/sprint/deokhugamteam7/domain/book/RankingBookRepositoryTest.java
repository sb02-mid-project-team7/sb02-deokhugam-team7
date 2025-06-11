package com.sprint.deokhugamteam7.domain.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.deokhugamteam7.config.QueryDslConfig;
import com.sprint.deokhugamteam7.config.TestAuditingConfig;
import com.sprint.deokhugamteam7.domain.book.dto.condition.BookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.condition.PopularBookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponseBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponsePopularBookDto;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.repository.custom.RankingBookRepositoryImpl;
import java.time.LocalDate;
import java.time.LocalDateTime;
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


  @BeforeEach
  void setUp() {
    LocalDate date = LocalDate.of(2025, 6, 10);
    Book book = Book.create(
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

    // when
    CursorPageResponseBookDto result1 = rankingBookRepository.findAllByKeyword(cond);
    cond.setDirection("asc");
    CursorPageResponseBookDto result2 = rankingBookRepository.findAllByKeyword(cond);
    cond.setCursor("0");
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
  void findAllByKeyword_reviewCount_success() {
    // given
    BookCondition cond = new BookCondition();
    cond.setOrderBy("reviewCount");

    // when
    CursorPageResponseBookDto result1 = rankingBookRepository.findAllByKeyword(cond);
    cond.setDirection("asc");
    CursorPageResponseBookDto result2 = rankingBookRepository.findAllByKeyword(cond);
    cond.setCursor("0");
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
  void findPopularBooks_default_Success() {
    // given
    PopularBookCondition cond = new PopularBookCondition();
    // when
    CursorPageResponsePopularBookDto result1 = rankingBookRepository.findPopularBooks(cond);
    // then
    assertThat(result1.content()).hasSize(1);
    assertThat(result1.content().get(0).title()).isEqualTo("제목");
  }
}
