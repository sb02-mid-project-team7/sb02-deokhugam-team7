package com.sprint.deokhugamteam7.domain.user.entity;

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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "user_score", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "period"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UserScore {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private Period period;

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(nullable = false)
  private double score;

  @Column(name = "review_score_sum")
  private double reviewScoreSum;

  @Column(name = "like_count")
  private long likeCount;

  @Column(name = "comment_count")
  private long commentCount;

  @Column(name = "rank")
  private Long rank;

  private static double calculateScore(double reviewScoreSum, long likeCount, long commentCount) {
    return reviewScoreSum * 0.5 + likeCount * 0.2 + commentCount * 0.3;
  }

  public static UserScore create(User user, Period period, double reviewScoreSum,
      long likeCount, long commentCount) {

    UserScore userScore = new UserScore();
    userScore.user = user;
    userScore.period = period;
    userScore.score = calculateScore(reviewScoreSum, likeCount, commentCount);
    userScore.reviewScoreSum = reviewScoreSum;
    userScore.likeCount = likeCount;
    userScore.commentCount = commentCount;
    userScore.rank = null;

    return userScore;
  }

  public void updateScores(double reviewScoreSum, long likeCount, long commentCount) {
    this.reviewScoreSum = reviewScoreSum;
    this.likeCount = likeCount;
    this.commentCount = commentCount;
    this.score = calculateScore(reviewScoreSum, likeCount, commentCount);
  }

  public boolean isSameScores(double reviewScoreSum, long likeCount, long commentCount) {
    return this.reviewScoreSum == reviewScoreSum &&
        this.likeCount == likeCount &&
        this.commentCount == commentCount &&
        this.score == calculateScore(reviewScoreSum, likeCount, commentCount);
  }

  public void updateRank(long rank) {
    this.rank = rank;
  }

  public void updateId(UUID id) {
    this.id = id;
  }

}