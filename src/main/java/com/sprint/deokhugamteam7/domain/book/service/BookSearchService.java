package com.sprint.deokhugamteam7.domain.book.service;

import com.sprint.deokhugamteam7.constant.Period;
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
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BookSearchService {

  private final ReviewRepository reviewRepository;
  private final RankingBookRepository rankingBookRepository;

  @Transactional
  @Scheduled(cron = "0 0/1 * * * *")
//  @Scheduled(cron = "0 0 09 * * *")
  public void updateRanking() {
    LocalDateTime now = LocalDateTime.now();
    List<RankingBook> rankingBooks = rankingBookRepository.findAll();
    for (RankingBook rankingBook : rankingBooks) {
      rankingBook.reset();
      LocalDateTime afterDate = calculateDateTime(now, rankingBook.getPeriod());
      List<Review> between = reviewRepository.findAllByBookAndCreatedAtBetweenAndIsDeletedIsFalse(
              rankingBook.getBook(), afterDate,
          now);
      between.forEach(review -> rankingBook.update(review.getRating(), false));
    }
    rankingBookRepository.saveAll(rankingBooks);
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
    return rankingBookRepository.findAllByKeyword(condition);
  }

  @Transactional(readOnly = true)
  public CursorPageResponsePopularBookDto findPopularBooks(PopularBookCondition condition) {
    return rankingBookRepository.findPopularBooks(condition);
  }
}
