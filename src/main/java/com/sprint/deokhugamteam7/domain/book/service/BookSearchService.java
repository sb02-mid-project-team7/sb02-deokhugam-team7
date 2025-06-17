package com.sprint.deokhugamteam7.domain.book.service;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.dto.condition.BookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.condition.PopularBookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponseBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponsePopularBookDto;
import com.sprint.deokhugamteam7.domain.book.entity.RankingBook;
import com.sprint.deokhugamteam7.domain.book.repository.RankingBookRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BookSearchService {

  private final RankingBookRepository rankingBookRepository;

  @Transactional(readOnly = true)
  public CursorPageResponseBookDto findAll(BookCondition condition) {
    return rankingBookRepository.findAllByKeyword(condition);
  }

  @Transactional(readOnly = true)
  public CursorPageResponsePopularBookDto findPopularBooks(PopularBookCondition condition) {
    return rankingBookRepository.findPopularBooks(condition);
  }

  public void updateRanksForPeriod(Period period) {
    List<RankingBook> rankingBooks = rankingBookRepository.findAllByPeriodOrderByScoreDesc(period);
    if (rankingBooks.isEmpty()) {
      return;
    }

    int rank = 1;

    for (RankingBook current : rankingBooks) {
      current.updateRank(rank++);
    }

    rankingBookRepository.saveAll(rankingBooks);
  }
}
