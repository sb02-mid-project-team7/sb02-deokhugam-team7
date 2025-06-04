package com.sprint.deokhugamteam7.domain.book.service;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.dto.BookDto;
import com.sprint.deokhugamteam7.domain.book.dto.FindPopularBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.PopularBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.condition.BookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.condition.PopularBookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponseBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponsePopularBookDto;
import com.sprint.deokhugamteam7.domain.book.entity.RankingBook;
import com.sprint.deokhugamteam7.domain.book.repository.RankingBookRepository;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookSearchService {

  private final ReviewRepository reviewRepository;
  private final RankingBookRepository rankingBookRepository;

  @Transactional
  @Scheduled(cron = "0 0/1 * * * *")
//  @Scheduled(cron = "0 0 09 * * *")
  public void updateRanking() {
    log.info("[Book Search Service] Update Ranking Books");
    LocalDateTime now = LocalDateTime.now();
    List<RankingBook> rankingBooks = rankingBookRepository.findAll();
    for (RankingBook rankingBook : rankingBooks) {
      rankingBook.reset();
      LocalDateTime afterDate = calculateDateTime(now, rankingBook.getPeriod());
      List<Review> between = reviewRepository.findAllByBookAndCreatedAtBetween(
          rankingBook.getBook(), afterDate,
          now);
      between.forEach(review -> rankingBook.update(review.getRating(), false));
    }
    rankingBookRepository.saveAll(rankingBooks);
    log.info("[Book Search Service] Updated Successfully");
  }

  private LocalDateTime calculateDateTime(LocalDateTime now, Period period) {
    if (period.equals(Period.DAILY)) {
      return now.minusDays(1);
    } else if (period.equals(Period.WEEKLY)) {
      return now.minusWeeks(1);
    } else if (period.equals(Period.MONTHLY)) {
      return now.minusMonths(1);
    } else {
      //TODO 전체를 가져올 로직이 생각이 안나서 일단 1년까지 가져오는 걸로
      return now.minusYears(1);
    }
  }

  @Transactional(readOnly = true)
  public CursorPageResponseBookDto findAll(BookCondition condition) {
    Sort.Direction direction = Sort.Direction.fromString(condition.getDirection());
    String orderBy = condition.getOrderBy();
    Sort sort;

    if (orderBy.equals("title")) {
      sort = Sort.by(direction, "book.title");
    } else if (orderBy.equals("publishedDate")) {
      sort = Sort.by(direction, "book.publishedDate");
    } else {
      sort = Sort.by(direction, orderBy);
    }

    Pageable pageable = PageRequest.of(0, condition.getLimit(), sort);

    LocalDateTime cursor = Optional.ofNullable(condition.getCursor()).map(LocalDateTime::parse)
        .orElse(LocalDateTime.now());

    log.info("[Book Search Service] findAll condition - keyword : {}, direction : {}",
        condition.getKeyword(), direction);

    Slice<BookDto> bookSlice = rankingBookRepository.findAllByKeyword(condition.getKeyword(),
        cursor, pageable).map(findBookDto -> {
      BookDto bookDto = BookDto.from(findBookDto);
      log.info("BookDto - title {}, reviewCount {}, rating {}", bookDto.title(),
          bookDto.reviewCount(), bookDto.rating());
      return bookDto;
    });

    return CursorPageResponseBookDto.of(bookSlice, condition.getKeyword());
  }

  @Transactional(readOnly = true)
  public CursorPageResponsePopularBookDto findPopularBooks(PopularBookCondition condition) {
    Period keyword = Period.valueOf(condition.getPeriod());
    String direction = condition.getDirection();
    int limit = condition.getLimit();

    Sort sort = Sort.by(Sort.Direction.fromString(condition.getDirection()), "score");
    Pageable pageable = PageRequest.of(0, limit, sort);

    log.info(
        "[Book Search Service] findPopularBooks condition - period : {}, direction : {}, size : {}",
        keyword, direction, limit);

    Slice<FindPopularBookDto> popularBooks = rankingBookRepository.findPopularBooks(keyword,
        pageable);

    //커서 10일 경우, -9 + 현재 인덱스(1~10)
    //커서 20일 경우, -9 + 현재 인덱스(11~20)
    int cursor = Integer.parseInt(condition.getCursor());
    Slice<PopularBookDto> dtoSlice = popularBooks.map(book -> {
          PopularBookDto popularBookDto = PopularBookDto.from(book,
              cursor + popularBooks.getContent().indexOf(book) - 9);
      log.info("popularBookDto - title {}, score {}, rank {}", popularBookDto.title(),
          popularBookDto.score(), popularBookDto.rank());
      return popularBookDto;
        }
    );

    return CursorPageResponsePopularBookDto.from(dtoSlice, String.valueOf(cursor + 10));
  }
}
