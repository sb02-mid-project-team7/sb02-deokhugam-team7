package com.sprint.deokhugamteam7.domain.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.dto.condition.BookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.condition.PopularBookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponseBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponsePopularBookDto;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.entity.RankingBook;
import com.sprint.deokhugamteam7.domain.book.repository.RankingBookRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookSearchServiceUnitTest {

  @Mock
  private RankingBookRepository rankingBookRepository;

  @InjectMocks
  private BookSearchService bookSearchService;

  private Book dummyBook1;
  private Book dummyBook2;
  private Book dummyBook3;
  private Book dummyBook4;

  @BeforeEach
  void setUp() {
    LocalDate now = LocalDate.now();
    dummyBook1 = Book.create("a", "b", "c", now).build();
    dummyBook2 = Book.create("aa", "bb", "cc", now).build();
    dummyBook3 = Book.create("aaa", "bbb", "ccc", now).build();
    dummyBook4 = Book.create("aaaa", "bbbb", "cccc", now).build();
  }

  @Test
  void findAllTest() {
    // given
    BookCondition cond = new BookCondition();
    cond.setKeyword("aaa");
    when(bookSearchService.findAll(cond)).thenReturn(mock(CursorPageResponseBookDto.class));
    // when
    CursorPageResponseBookDto result = bookSearchService.findAll(cond);
    // then
    assertNotNull(result);
  }

  @Test
  void findPopularBooksTest() {
    // given
    PopularBookCondition cond = new PopularBookCondition();
    when(rankingBookRepository.findPopularBooks(cond)).thenReturn(mock(
        CursorPageResponsePopularBookDto.class));
    // when
    CursorPageResponsePopularBookDto result = bookSearchService.findPopularBooks(cond);
    // then
    assertNotNull(result);
  }

  @Test
  void updateRanksForPeriod_assignsCorrectRanks_whenScoresHaveTies() {
    // given
    Period period = Period.DAILY;

    RankingBook b1 = RankingBook.create(dummyBook1, period);
    b1.updateScore(100.0);

    RankingBook b2 = RankingBook.create(dummyBook2, period);
    b2.updateScore(90.0);

    RankingBook b3 = RankingBook.create(dummyBook3, period);
    b3.updateScore(90.0);

    RankingBook b4 = RankingBook.create(dummyBook4, period);
    b4.updateScore(80.0);

    List<RankingBook> list = List.of(b1, b2, b3, b4);
    when(rankingBookRepository.findAllByPeriodOrderByScoreDesc(period))
        .thenReturn(list);

    // when
    bookSearchService.updateRanksForPeriod(period);

    // then
    assertThat(b1.getRank()).isEqualTo(1);  // 최고점 100 → 1등
    assertThat(b2.getRank()).isEqualTo(2);  // 다음 점수 90 → 2등
    assertThat(b3.getRank()).isEqualTo(3);  //  3등
    assertThat(b4.getRank()).isEqualTo(4);  // 다음은 4등

    verify(rankingBookRepository).saveAll(list);
  }

  @Test
  void updateRanksForPeriod_doesNothing_whenNoEntries() {
    // given
    Period period = Period.WEEKLY;
    when(rankingBookRepository.findAllByPeriodOrderByScoreDesc(period))
        .thenReturn(Collections.emptyList());

    // when
    bookSearchService.updateRanksForPeriod(period);

    // then
    verify(rankingBookRepository, never()).saveAll(any());
  }
}
