package com.sprint.deokhugamteam7.domain.book.entity;

import com.sprint.deokhugamteam7.constant.Period;
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

  @Setter
  @Column(name = "rank")
  private long rank;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Period period;

  @Column(name = "score")
  private double score;

  @Column(name = "total_rating")
  private int totalRating;

  @Column(name = "review_count")
  private long reviewCount;

  private RankingBook(Period period) {
    this.period = period;
    this.rank = 0;
    this.score = 0.0;
    this.totalRating = 0;
    this.reviewCount = 0;
  }

  public static RankingBook create(Period period) {
    return new RankingBook(period);
  }

  public void update(int rating) {
    totalRating += rating;
    if (rating >= 0) {
      this.reviewCount++;
    } else {
      this.reviewCount--;
    }
    this.score = (reviewCount * 0.4) + ((double) totalRating / reviewCount) * 0.6;
  }

  public double getRating() {
    return reviewCount == 0 ? 0.0 : (double) totalRating / reviewCount;
  }

}
