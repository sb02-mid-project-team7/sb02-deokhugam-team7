package com.sprint.deokhugamteam7.domain.review.entity;

import com.sprint.deokhugamteam7.constant.Period;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "ranking_reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class RankingReview {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "review_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Review review;

  @Column(nullable = false)
  private double score;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Period period;

  @Column(name="review_created_at",nullable = false)
  private LocalDateTime reviewCreatedAt;

  public static RankingReview create(Review review, Double score, Period period) {
    RankingReview ranking = new RankingReview();
    ranking.review = review;
    ranking.score = score;
    ranking.period = period;
    ranking.reviewCreatedAt = review.getCreatedAt();

    return ranking;
  }

  public void update(Double score) {
    this.score = score;
  }
}
