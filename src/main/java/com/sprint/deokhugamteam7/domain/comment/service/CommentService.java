package com.sprint.deokhugamteam7.domain.comment.service;

import com.sprint.deokhugamteam7.constant.NotificationType;
import com.sprint.deokhugamteam7.domain.comment.dto.CommentDto;
import com.sprint.deokhugamteam7.domain.comment.dto.request.CommentCreateRequest;
import com.sprint.deokhugamteam7.domain.comment.dto.request.CommentUpdateRequest;
import com.sprint.deokhugamteam7.domain.comment.dto.response.CursorPageResponseCommentDto;
import com.sprint.deokhugamteam7.domain.comment.entity.Comment;
import com.sprint.deokhugamteam7.domain.comment.repository.CommentRepository;
import com.sprint.deokhugamteam7.domain.notification.entity.Notification;
import com.sprint.deokhugamteam7.domain.notification.repository.NotificationRepository;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewRepository;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
//@Slf4j
public class CommentService {

	private final CommentRepository commentRepository;
	private final UserRepository userRepository;
	private final ReviewRepository reviewRepository;
	private final NotificationRepository notificationRepository;

	@Transactional
	public CommentDto create(CommentCreateRequest commentCreateRequest) {
//		log.info(
//			"[CommentService] create Comment request: reviewId={}, userId={}, contentSubstring={}",
//			commentCreateRequest.reviewId(),
//			commentCreateRequest.userId(),
//			commentCreateRequest.content() != null ? commentCreateRequest.content()
//				.substring(0, Math.min(commentCreateRequest.content().length(), 20)) : "null"
//		);

		UUID userId = commentCreateRequest.userId();
		UUID reviewId = commentCreateRequest.reviewId();

		User user = userRepository.findById(userId).orElseThrow(
			() -> new EntityNotFoundException("user not found")
		);
		if (user.isDeleted()) {
			throw new IllegalArgumentException("user is deleted");
		}

		Review review = reviewRepository.findById(reviewId).orElseThrow(
			() -> new EntityNotFoundException("review not found")
		);
		if (review.getIsDeleted()) {
			throw new IllegalArgumentException("review is deleted");
		}

		String content = commentCreateRequest.content();

		Comment newComment = Comment.create(user, review, content);
		Comment savedComment = commentRepository.save(newComment);
		// 댓글 생성 시 리뷰의 댓글 수를 증가 + 알림 생성 해야함.
//		log.info("알림 생성 진행: userId: {}", review.getUser());
		Notification notification = Notification.create(review.getUser(), review,
			NotificationType.COMMENT.formatMessage(user, newComment));
		notificationRepository.save(notification);
//		log.info("알림 생성 성공");

		return CommentDto.from(savedComment);
	}

	@Transactional
	public CommentDto update(UUID commentId, UUID userId,
		CommentUpdateRequest commentUpdateRequest) {
//		log.info(
//			"[CommentService] update Comment request: commentId={}, userId={}, newContentSubstring={}",
//			commentId,
//			userId,
//			commentUpdateRequest.content() != null ? commentUpdateRequest.content()
//				.substring(0, Math.min(commentUpdateRequest.content().length(), 20)) : "null"
//		);

		Comment comment = commentRepository.findById(commentId).orElseThrow(
			() -> new EntityNotFoundException("comment not found")
		);

//		log.info("✅ comment.getUser().getId(): {}", comment.getUser().getId());
//		log.info("✅ userId: {}", userId);
		if (!comment.getUser().getId().equals(userId)) {
			throw new IllegalArgumentException("해당 댓글을 수정할 권한이 없습니다.");
		}

		String content = commentUpdateRequest.content();
		comment.update(content);

		if (content == null || content.isBlank()) { // isBlank() 사용!
			throw new IllegalArgumentException("댓글은 공백일 수 없습니다.");
		}

		return CommentDto.from(comment);
	}

	@Transactional
	public void deleteHard(UUID commentId, UUID userId) {
//		log.info("[CommentService] deleteHard Comment request: commentId={}, userId={}", commentId,
//			userId);

		Comment comment = commentRepository.findById(commentId).orElseThrow(
			() -> new EntityNotFoundException("comment not found")
		);

		if (!comment.getUser().getId().equals(userId)) {
			throw new IllegalArgumentException("해당 댓글을 삭제할 권한이 없습니다.");
		}

		commentRepository.deleteById(commentId);
		// 댓글 삭제 후 리뷰의 댓글 수를 감소 시켜야함
	}

	@Transactional
	public void deleteSoft(UUID commentId, UUID userId) {
//		log.info("[CommentService] deleteSoft Comment request: commentId={}, userId={}", commentId,
//			userId);

		Comment comment = commentRepository.findById(commentId).orElseThrow(
			() -> new EntityNotFoundException("comment not found")
		);

		if (!comment.getUser().getId().equals(userId)) {
			throw new IllegalArgumentException("해당 댓글을 삭제할 권한이 없습니다.");
		}

		// 삭제시에 리뷰의 댓글 수를 감소시켜야함 .
		comment.delete();
	}

	@Transactional(readOnly = true)
	public CursorPageResponseCommentDto getCommentList(UUID reviewId, String direction,
		UUID cursorId, LocalDateTime createdAt, int limit) {
//		log.info(
//			"[CommentService] getCommentList request: reviewId={}, direction={}, cursorId={}, createdAt={}, limit={}",
//			reviewId, direction, cursorId, createdAt, limit);

		int queryLimit = limit + 1; // has next를 위해 limit에 1을 더한 후 더 보여줄 페이지가 있는지 판단

		// 1. 첫번째 페이지인 경우는 nextCursor nextAfter 가 없음 .
		List<Comment> comments = cursorId == null || createdAt == null ?
			commentRepository.findFirstPage(reviewId, direction, queryLimit) :
			commentRepository
				.findNextPage(reviewId, direction, cursorId, createdAt, queryLimit);

		boolean hasNext = comments.size() > limit;
		// limit+1개 만큼 가져왔으니까 다시 limit 개로 변경
		List<Comment> currentPageItems = hasNext ? comments.subList(0, limit) : comments;

		UUID nextCursor = null;
		LocalDateTime nextAfter = null;

		List<CommentDto> commentsDtos = currentPageItems.stream()
			.map(CommentDto::from)
			.toList();
		int size = commentsDtos.size();

		if (hasNext && !commentsDtos.isEmpty()) {
			CommentDto lastElement = commentsDtos.get(commentsDtos.size() - 1); // 현재 페이지의 마지막 요소
			nextCursor = lastElement.id();
			nextAfter = lastElement.createdAt();
		}

		// 리뷰 id를 사용해서 count 구하기 .
		Long totalElements = commentRepository.countByReviewId(reviewId);

//		log.info("nextCursor: {}", nextCursor);
//		log.info("nextAfter: {}", nextAfter);

		return new CursorPageResponseCommentDto(
			commentsDtos, // 현재 페이지
			nextCursor,
			nextAfter,
			size,
			totalElements,
			hasNext
		);
	}

	@Transactional(readOnly = true)
	public CommentDto getComment(UUID commentId) {
//		log.info("[CommentService] getComment request: commentId={}", commentId);
		Comment comment = commentRepository.findById(commentId).orElseThrow(
			() -> new EntityNotFoundException("comment not found")
		);

		return CommentDto.from(comment);
	}
}
