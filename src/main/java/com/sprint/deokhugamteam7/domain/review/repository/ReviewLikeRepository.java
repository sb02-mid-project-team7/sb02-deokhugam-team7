package com.sprint.deokhugamteam7.domain.review.repository;

import com.sprint.deokhugamteam7.domain.review.entity.ReviewLike;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, UUID> {

  @Query("SELECT COUNT(rl) FROM ReviewLike rl WHERE rl.review.id = :reviewId")
  int countByReviewId(@Param("reviewId") UUID reviewId);

  @Query("SELECT CASE WHEN COUNT(rl) > 0 THEN true ELSE false END "
      + "FROM ReviewLike rl "
      + "WHERE rl.user.id = :userId AND rl.review.id = :reviewId")
  boolean existsByUserIdAndReviewId(@Param("userId") UUID userId, @Param("reviewId") UUID reviewId);

  Optional<ReviewLike> findByReviewIdAndUserId(UUID reviewId, UUID userId);

  @Query("SELECT rl.review.id "
      + "FROM ReviewLike rl "
      + "WHERE rl.user.id = :userId "
      + "AND rl.review.id IN :reviewIds")
  Set<UUID> findReviewIdsLikedByUser(@Param("userId") UUID userId,
      @Param("reviewIds") List<UUID> reviewIds);
}
