package com.sprint.deokhugamteam7.domain.comment.repository;

import com.sprint.deokhugamteam7.domain.comment.entity.Comment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

  @Query("SELECT COUNT(c) FROM Comment c WHERE c.review.id = :id AND c.isDeleted = false")
  int countByReviewIdAndIsDeletedFalse(@Param("id") UUID reviewId);
}
