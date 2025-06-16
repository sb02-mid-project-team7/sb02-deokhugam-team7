package com.sprint.deokhugamteam7.domain.book.entity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sprint.deokhugamteam7.constant.Period;
import org.junit.jupiter.api.Test;

public class RankingBookEntityTest {

  @Test
  void createRankingBookSuccess() {
    // given
    Period period = Period.DAILY;
    // when
    RankingBook rankingBook = RankingBook.create(period);
    // then
    assertAll(
        () -> assertEquals(period, rankingBook.getPeriod()),
        () -> assertEquals(0.0, rankingBook.getScore()),
        () -> assertEquals(0, rankingBook.getTotalRating()),
        () -> assertEquals(0, rankingBook.getReviewCount()),
        () -> assertEquals(0.0, rankingBook.getRating())
    );
  }


  @Test
  void getRatingWithNoReviewCount() {
    // given
    Period period = Period.DAILY;
    RankingBook rankingBook = RankingBook.create(period);
    // when
    double rating = rankingBook.getRating();
    // then
    assertEquals(0.0, rating);
  }
}
