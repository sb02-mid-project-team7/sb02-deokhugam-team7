package com.sprint.deokhugamteam7.domain.book.service;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.dto.BookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.BookDto;
import com.sprint.deokhugamteam7.domain.book.dto.PopularBookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponseBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponsePopularBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.response.PopularBookDto;
import com.sprint.deokhugamteam7.domain.book.entity.RankingBook;
import com.sprint.deokhugamteam7.domain.book.repository.RankingBookRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookSearchService {

  private final RankingBookRepository rankingBookRepository;

  @Transactional
  public void updateRanking() {
    // 오늘 기준 일일, 주간, 월간, 역대 랭킹 생성
    // 오늘 - period에 적힌 기간 이전 날짜의 리뷰를 몽땅 가져온 뒤, 평점 삭제
    // 매일 오전 9시에 일괄적으로 갱신

  }

  @Transactional(readOnly = true)
  public CursorPageResponseBookDto findAll(BookCondition condition) {
    Sort.Direction direction = Sort.Direction.fromString(condition.getDirection());
    Sort sort = Sort.by(direction, condition.getOrderBy());
    Pageable pageable = PageRequest.of(0, condition.getLimit(), sort);

    LocalDateTime cursor =Optional.ofNullable(condition.getCursor()).map(LocalDateTime::parse)
        .orElse(LocalDateTime.now());

    Slice<BookDto> bookSlice = rankingBookRepository.findAllByKeyword(condition.getKeyword(),
            cursor, pageable)
        .map(BookDto::from);

    return CursorPageResponseBookDto.of(bookSlice, condition.getKeyword());
  }

  @Transactional(readOnly = true)
  public CursorPageResponsePopularBookDto findPopularBooks(PopularBookCondition condition) {
    Period keyword = Period.valueOf(condition.getPeriod());
    String direction = condition.getDirection();
    int limit = condition.getLimit();

    Sort sort = Sort.by(Sort.Direction.fromString(condition.getDirection()), "rank");
    Pageable pageable = PageRequest.of(0, limit, sort);

    log.info("[Basic Book Service] condition - period : {}, direction : {}, size : {}",
        keyword, direction, limit);

    Slice<RankingBook> popularBooks = rankingBookRepository.findPopularBooks(keyword, pageable);

    //커서 10일 경우, -9 + 현재 인덱스(1~10)
    //커서 20일 경우, -9 + 현재 인덱스(11~20)
    int cursor = Integer.parseInt(condition.getCursor());
    Slice<PopularBookDto> dtoSlice = popularBooks.map(book ->
        PopularBookDto.from(book, cursor + popularBooks.getContent().indexOf(book) - 9)
    );

    return CursorPageResponsePopularBookDto.from(dtoSlice, String.valueOf(cursor + 10));
  }
}
