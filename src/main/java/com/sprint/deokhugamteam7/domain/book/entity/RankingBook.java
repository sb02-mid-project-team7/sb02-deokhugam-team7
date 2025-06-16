package com.sprint.deokhugamteam7.domain.book.entity;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ranking_books")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RankingBook {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;


  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Period period;

  @Column(name = "score")
  private double score;

  @Column(name = "total_rating")
  private int totalRating;

  @Column(name = "review_count")
  private long reviewCount;

  @Column(name = "rating")
  private double rating;

  @Column(name = "rankg")
  private double rank;

  private RankingBook(Period period) {
    this.period = period;
    this.rating = 0.0;
    this.score = 0.0;
    this.totalRating = 0;
    this.reviewCount = 0;
    this.rank = 0;
  }

  private RankingBook(Period period, double rating, double score) {
    this.period = period;
    this.rating = rating;
    this.score = score;
    this.totalRating = 0;
    this.reviewCount = 0;
    this.rank = 0;
  }

  public static RankingBook create(Period period) {
    return new RankingBook(period);
  }

  public static RankingBook create(Period period,double rating, double score) {
    return new RankingBook(period, rating, score);
  }

  public void updateScore(int rating, boolean isDeleted) {
    if (!isDeleted) {
      totalRating += rating;
      reviewCount++;
    } else {
      totalRating -= rating;
      reviewCount--;
    }
    if (reviewCount > 0) {
      this.rating = (double) totalRating / reviewCount;
      this.score = (reviewCount * 0.4) + (this.rating * 0.6);
    } else {
      this.rating = 0.0;
      this.score = 0.0;
    }
  }

  public void updateScore(double score) {
    this.score = score;
  }

  public void updateRank(long rank) {
    this.rank = rank;
  }

  public double getRating() {
    return reviewCount == 0 ? 0.0 : (double) totalRating / reviewCount;
  }

  public void reset() {
    this.score = 0.0;
    this.totalRating = 0;
    this.reviewCount = 0;
    this.rating = 0.0;
  }

  public void reCalculate() {
    List<Review> reviews = this.book.getReviews();
    reset();
    reviews.stream()
        .filter(review -> !review.getIsDeleted())
        .forEach(review -> updateScore(review.getRating(), false));
  }

}
