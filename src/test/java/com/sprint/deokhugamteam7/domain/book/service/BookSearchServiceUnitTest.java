package com.sprint.deokhugamteam7.domain.book.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.domain.book.dto.condition.BookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.condition.PopularBookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponseBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponsePopularBookDto;
import com.sprint.deokhugamteam7.domain.book.repository.RankingBookRepository;
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
