package com.sprint.deokhugamteam7.domain.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.dto.BookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.FindBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.FindPopularBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.PopularBookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponseBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponsePopularBookDto;
import com.sprint.deokhugamteam7.domain.book.repository.RankingBookRepository;
import com.sprint.deokhugamteam7.domain.book.service.BookSearchService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

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

    Slice<FindBookDto> emptySlice =
        new SliceImpl<>(List.of(), PageRequest.of(0, 10), false);

    when(rankingBookRepository.findAllByKeyword(
        eq(cond.getKeyword()),
        any(LocalDateTime.class),
        any(Pageable.class))).thenReturn(emptySlice);
    // when
    CursorPageResponseBookDto result = bookSearchService.findAll(cond);
    // then
    assertNotNull(result);
    assertEquals(cond.getKeyword(), result.nextCursor());
  }

  @Test
  void findPopularBooksTest() {
    // given
    PopularBookCondition cond = new PopularBookCondition();
    Slice<FindPopularBookDto> emptySlice =
        new SliceImpl<>(List.of(), PageRequest.of(0, 10), false);
    when(rankingBookRepository.findPopularBooks(eq(Period.valueOf(cond.getPeriod())),
        any(Pageable.class))).thenReturn(emptySlice);
    // when
    CursorPageResponsePopularBookDto result = bookSearchService.findPopularBooks(cond);
    // then
    assertNotNull(result);
  }
}
