package com.sprint.deokhugamteam7.domain.user.repository;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.user.entity.UserScore;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserScoreRepository extends JpaRepository<UserScore, UUID> {
  List<UserScore> findAllByPeriodOrderByScoreDesc(Period period);
  List<UserScore> findAllByPeriod(Period period);

  @Modifying
  @Transactional
  @Query(
      value = """
            INSERT INTO user_score (id, user_id, period, created_at, updated_at, score, review_score_sum, like_count, comment_count, rank)
            VALUES (:id, :userId, :period, NOW(), NOW(), :score, :reviewScoreSum, :likeCount, :commentCount, NULL)
            ON CONFLICT (user_id, period)
            DO UPDATE SET
              score = EXCLUDED.score,
              review_score_sum = EXCLUDED.review_score_sum,
              like_count = EXCLUDED.like_count,
              comment_count = EXCLUDED.comment_count,
              updated_at = NOW()
            """,
      nativeQuery = true
  )
  void upsertUserScore(
      @Param("id") UUID id,
      @Param("userId") UUID userId,
      @Param("period") String period,
      @Param("score") double score,
      @Param("reviewScoreSum") double reviewScoreSum,
      @Param("likeCount") long likeCount,
      @Param("commentCount") long commentCount
  );
}