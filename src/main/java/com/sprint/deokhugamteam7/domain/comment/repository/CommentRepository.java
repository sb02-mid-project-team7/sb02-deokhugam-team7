package com.sprint.deokhugamteam7.domain.comment.repository;

import com.sprint.deokhugamteam7.domain.comment.entity.Comment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

	// 다음 페이지 조회용 (커서 있음)
	@Query(
		value =
			"SELECT c.id, c.user_id, c.review_id, c.content, c.is_deleted, c.created_at, c.updated_at "
				+
				"FROM comments c " +
				"WHERE c.review_id = :reviewId AND c.is_deleted = FALSE " +
				"  AND (c.created_at < :createdAt OR " +
				"       (c.created_at = :createdAt AND c.id < :cursorId)) " +
				"ORDER BY c.created_at DESC, c.id DESC " +
				"limit :limit",
		nativeQuery = true
	)
	List<Comment> findAllInfiniteScroll(
		@Param("reviewId") UUID reviewId,
		@Param("direction") String direction,
		@Param("cursorId") UUID cursorId,
		@Param("createdAt") LocalDateTime createdAt,
		@Param("limit") int limit
	);

	// 첫 페이지 조회용 (커서 없음)
	@Query(
		value =
			"SELECT c.id, c.user_id, c.review_id, c.content, c.is_deleted, c.created_at, c.updated_at "
				+
				"FROM comments c " +
				"WHERE c.review_id = :reviewId AND c.is_deleted = FALSE " +
				"ORDER BY c.created_at DESC, c.id DESC " +
				"LIMIT :limit",
		nativeQuery = true
	)
	List<Comment> findAllInfiniteScroll(
		@Param("reviewId") UUID reviewId,
		@Param("direction") String direction,
		@Param("limit") int limit);

	@Query(
		value = "SELECT COUNT(*) FROM from ("
			+ "select comment_id from comments where review_id =: review_id AND is_deleted =: false"
			+ ") t",
		nativeQuery = true
	)
	Long countByReviewId(@Param("reviewId") UUID reviewId);
}
