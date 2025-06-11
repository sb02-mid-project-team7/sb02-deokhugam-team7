package com.sprint.deokhugamteam7.domain.book.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.dto.condition.BookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.condition.PopularBookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponseBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponsePopularBookDto;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.entity.RankingBook;
import com.sprint.deokhugamteam7.domain.book.repository.RankingBookRepository;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookSearchServiceUnitTest {

  @Mock
  private RankingBookRepository rankingBookRepository;

  @Mock
  private ReviewRepository reviewRepository;

  @InjectMocks
  private BookSearchService bookSearchService;

  @Test
  void updateRankingTest() {
    // given
    Book book = Book.create("a", "a", "a", LocalDate.now()).build();
    RankingBook daily = RankingBook.create(Period.DAILY);
    RankingBook weekly = RankingBook.create(Period.WEEKLY);
    RankingBook monthly = RankingBook.create(Period.MONTHLY);
    RankingBook allTile = RankingBook.create(Period.ALL_TIME);
    daily.setBook(book);
    weekly.setBook(book);
    monthly.setBook(book);
    allTile.setBook(book);
    List<RankingBook> mockRankingBooks = List.of(daily,weekly,monthly,allTile);
    when(rankingBookRepository.findAll()).thenReturn(mockRankingBooks);
    List<Review> mockReviews = new ArrayList<>();
    when(reviewRepository.findAllByBookAndCreatedAtBetweenAndIsDeletedIsFalse(eq(book), any(),
        any())).thenReturn(mockReviews);
    // when
    bookSearchService.updateRanking();
    // then

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
}
