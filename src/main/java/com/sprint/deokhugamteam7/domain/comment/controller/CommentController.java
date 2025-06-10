package com.sprint.deokhugamteam7.domain.comment.controller;

import com.sprint.deokhugamteam7.domain.comment.dto.CommentDto;
import com.sprint.deokhugamteam7.domain.comment.dto.request.CommentCreateRequest;
import com.sprint.deokhugamteam7.domain.comment.dto.request.CommentUpdateRequest;
import com.sprint.deokhugamteam7.domain.comment.dto.response.CursorPageResponseCommentDto;
import com.sprint.deokhugamteam7.domain.comment.service.CommentService;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {

	private final CommentService commentService;

	@PostMapping("/api/comments")
	public ResponseEntity<CommentDto> create(
		@RequestBody CommentCreateRequest commentCreateRequest) {
		CommentDto commentDto = commentService.create(commentCreateRequest);

		return ResponseEntity.status(HttpStatus.CREATED).body(commentDto);
	}

	@PatchMapping("/api/comments/{commentId}")
	public ResponseEntity<CommentDto> update(
		@PathVariable UUID commentId,
		@RequestHeader(value = "Deokhugam-Request-User-ID") UUID userId,
		@RequestBody CommentUpdateRequest commentUpdateRequest) {
		CommentDto commentDto = commentService.update(commentId, userId, commentUpdateRequest);

		return ResponseEntity.ok(commentDto);
	}

	@DeleteMapping("/api/comments/{commentId}/hard")
	public ResponseEntity<?> deleteHard(
		@PathVariable UUID commentId,
		@RequestHeader(value = "Deokhugam-Request-User-ID") UUID userId
	) {
		commentService.deleteHard(commentId, userId);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/api/comments/{commentId}")
	public ResponseEntity<?> deleteSoft(
		@PathVariable UUID commentId,
		@RequestHeader(value = "Deokhugam-Request-User-ID") UUID userId
	) {
		commentService.deleteSoft(commentId, userId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/api/comments")
	public ResponseEntity<CursorPageResponseCommentDto> getCommentList(
		@RequestParam UUID reviewId,
		@RequestParam(defaultValue = "DESC") String direction,
		@RequestParam(required = false) UUID cursor,
		@RequestParam(required = false) LocalDateTime after, // 이전 페이지의 마지막 요소 생성 시간.
		@RequestParam(defaultValue = "30") int limit
	) {
		// 시간순 정렬해놔야함 .
		// 마지막 요소의 ID + 마지막 요소의 생성시간 전달 .
		log.info("cursorID: {}, nextAfter: {}", cursor, after);
		CursorPageResponseCommentDto response = commentService.getCommentList(reviewId,
			direction, cursor, after, limit);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/api/comments/{commentId}")
	public ResponseEntity<CommentDto> getComment(
		@PathVariable UUID commentId
	) {
		CommentDto commentDto = commentService.getComment(commentId);

		return ResponseEntity.ok(commentDto);
	}
}
