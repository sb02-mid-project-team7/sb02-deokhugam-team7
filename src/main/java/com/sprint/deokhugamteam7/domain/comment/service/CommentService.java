package com.sprint.deokhugamteam7.domain.comment.service;

import com.sprint.deokhugamteam7.domain.comment.dto.CommentDto;
import com.sprint.deokhugamteam7.domain.comment.dto.request.CommentCreateRequest;
import com.sprint.deokhugamteam7.domain.comment.dto.request.CommentUpdateRequest;
import com.sprint.deokhugamteam7.domain.comment.dto.response.CursorPageResponseCommentDto;
import com.sprint.deokhugamteam7.domain.comment.entity.Comment;
import com.sprint.deokhugamteam7.domain.comment.repository.CommentRepository;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewRepository;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import com.sprint.deokhugamteam7.exception.comment.ForbiddenException;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

	private final CommentRepository commentRepository;
	private final UserRepository userRepository;
	private final ReviewRepository reviewRepository;

	public CommentDto create(CommentCreateRequest commentCreateRequest) {
		UUID userId = commentCreateRequest.userId();
		UUID reviewId = commentCreateRequest.reviewId();

		User user = userRepository.findById(userId).orElseThrow(
			() -> new EntityNotFoundException("user not found")
		);
		Review review = reviewRepository.findById(reviewId).orElseThrow(
			() -> new EntityNotFoundException("review not found")
		);

		String content = commentCreateRequest.content();

		Comment newComment = Comment.create(
			user,
			review,
			content
		);

		Comment savedComment = commentRepository.save(newComment);
		return CommentDto.from(savedComment);
	}

	public CommentDto update(UUID commentId, UUID userId,
		CommentUpdateRequest commentUpdateRequest) {
		Comment comment = commentRepository.findById(commentId).orElseThrow(
			() -> new EntityNotFoundException("comment not found")
		);

		if (comment.getUser().getId() != userId) {
			throw new ForbiddenException("해당 댓글을 수정할 권한이 없습니다.");
		}

		String content = commentUpdateRequest.content();
		comment.update(content);

		return CommentDto.from(comment);
	}

	public void deleteHard(UUID commentId, UUID userId) {
		Comment comment = commentRepository.findById(commentId).orElseThrow(
			() -> new EntityNotFoundException("comment not found")
		);

		if (comment.getUser().getId() != userId) {
			throw new ForbiddenException("해당 댓글을 삭제할 권한이 없습니다.");
		}

		commentRepository.deleteById(commentId);
	}

	public void deleteSoft(UUID commentId, UUID userId) {
		Comment comment = commentRepository.findById(commentId).orElseThrow(
			() -> new EntityNotFoundException("comment not found")
		);

		if (comment.getUser().getId() != userId) {
			throw new ForbiddenException("해당 댓글을 삭제할 권한이 없습니다.");
		}

		comment.delete();
	}


	public CursorPageResponseCommentDto getCommentList(UUID reviewId, String direction,
		String cursor,
		LocalDateTime after, int limit) {

		return null;
	}

	public CommentDto getComment(UUID commentId) {
		Comment comment = commentRepository.findById(commentId).orElseThrow(
			() -> new EntityNotFoundException("comment not found")
		);

		return CommentDto.from(comment);
	}
}
