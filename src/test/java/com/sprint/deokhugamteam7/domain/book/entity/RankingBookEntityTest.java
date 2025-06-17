package com.sprint.deokhugamteam7.domain.book.entity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sprint.deokhugamteam7.constant.Period;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RankingBookEntityTest {

  private LocalDate now;
  private Book book;
  private RankingBook daily;
  private RankingBook weekly;
  private RankingBook monthly;
  private RankingBook allTime;


  @BeforeEach
  void setUp() {
    now = LocalDate.now();
    book = Book.create("aaa", "bbb", "ccc", now).build();
    daily = RankingBook.create(book, Period.DAILY);
    weekly = RankingBook.create(book, Period.WEEKLY);
    monthly = RankingBook.create(book, Period.MONTHLY);
    allTime = RankingBook.create(book, Period.ALL_TIME, 3.0, 4.0, 5);
  }

  @Test
  void updateScore() {
    // given
    double score = 5.0;
    // when
    daily.updateScore(score);
    weekly.updateScore(score);
    monthly.updateScore(score);
    allTime.updateScore(score);
    // then
    assertAll(
        () -> assertEquals(5, daily.getScore()),
        () -> assertEquals(5, weekly.getScore()),
        () -> assertEquals(5, monthly.getScore()),
        () -> assertEquals(5, allTime.getScore())
    );
  }

  @Test
  void updateRank() {
    // given
    long rank = 1;
    // when
    daily.updateRank(rank);
    weekly.updateRank(rank);
    monthly.updateRank(rank);
    allTime.updateRank(rank);
    // then
    assertAll(
        () -> assertEquals(rank, daily.getRank()),
        () -> assertEquals(rank, weekly.getRank()),
        () -> assertEquals(rank, monthly.getRank()),
        () -> assertEquals(rank, allTime.getRank())
    );
  }

  @Test
  void updateRating() {
    // given
    double rating = 1.0;
    // when
    daily.updateRating(rating);
    weekly.updateRating(rating);
    monthly.updateRating(rating);
    allTime.updateRating(rating);
    // then
    assertAll(
        () -> assertEquals(rating, daily.getRating()),
        () -> assertEquals(rating, weekly.getRating()),
        () -> assertEquals(rating, monthly.getRating()),
        () -> assertEquals(rating, allTime.getRating())
    );
  }

  @Test
  void updateReviewCount() {
    // given
    long reviewCount = 1;
    // when
    daily.updateReviewCount(reviewCount);
    weekly.updateReviewCount(reviewCount);
    monthly.updateReviewCount(reviewCount);
    allTime.updateReviewCount(reviewCount);
    // then
    assertAll(
        () -> assertEquals(reviewCount, daily.getReviewCount()),
        () -> assertEquals(reviewCount, weekly.getReviewCount()),
        () -> assertEquals(reviewCount, monthly.getReviewCount()),
        () -> assertEquals(reviewCount, allTime.getReviewCount())
    );
  }
}
