package com.sprint.deokhugamteam7.domain.comment.controller;

import com.sprint.deokhugamteam7.domain.comment.dto.CommentDto;
import com.sprint.deokhugamteam7.domain.comment.dto.request.CommentCreateRequest;
import com.sprint.deokhugamteam7.domain.comment.dto.request.CommentUpdateRequest;
import com.sprint.deokhugamteam7.domain.comment.dto.response.CursorPageResponseCommentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "댓글 관리", description = "댓글 관련 API")
public interface CommentApi {

	@Operation(summary = "댓글 등록", description = "새로운 댓글을 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "댓글 등록 성공",
			content = @Content(schema = @Schema(implementation = CommentDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패)"),
		@ApiResponse(responseCode = "404", description = "리뷰 또는 사용자 정보 없음"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	ResponseEntity<CommentDto> create(
		@Parameter(description = "댓글 생성 요청 정보", required = true)
		@Valid @RequestBody CommentCreateRequest commentCreateRequest
	);

	@Operation(summary = "댓글 수정", description = "본인이 작성한 댓글을 수정합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "댓글 수정 성공",
			content = @Content(schema = @Schema(implementation = CommentDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패, 요청자 ID 누락)"),
		@ApiResponse(responseCode = "403", description = "댓글 수정 권한 없음"),
		@ApiResponse(responseCode = "404", description = "댓글 정보 없음"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	ResponseEntity<CommentDto> update(
		@Parameter(description = "수정할 댓글의 ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
		@PathVariable UUID commentId,
		@Parameter(description = "요청자 ID (헤더)", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
		@RequestHeader("Deokhugam-Request-User-ID") UUID userId,
		@Parameter(description = "수정할 댓글 내용", required = true)
		@Valid @RequestBody CommentUpdateRequest commentUpdateRequest
	);

	@Operation(summary = "댓글 논리 삭제", description = "본인이 작성한 댓글을 논리적으로 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "댓글 삭제 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 (요청자 ID 누락)"),
		@ApiResponse(responseCode = "403", description = "댓글 삭제 권한 없음"),
		@ApiResponse(responseCode = "404", description = "댓글 정보 없음"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	ResponseEntity<Void> deleteSoft(
		@Parameter(description = "삭제할 댓글의 ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
		@PathVariable UUID commentId,
		@Parameter(description = "요청자 ID (헤더)", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
		@RequestHeader("Deokhugam-Request-User-ID") UUID userId
	);

	@Operation(summary = "댓글 물리 삭제", description = "본인이 작성한 댓글을 물리적으로 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "댓글 삭제 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 (요청자 ID 누락)"),
		@ApiResponse(responseCode = "403", description = "댓글 삭제 권한 없음"),
		@ApiResponse(responseCode = "404", description = "댓글 정보 없음"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	ResponseEntity<Void> deleteHard(
		@Parameter(description = "삭제할 댓글의 ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
		@PathVariable UUID commentId,
		@Parameter(description = "요청자 ID (헤더)", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
		@RequestHeader("Deokhugam-Request-User-ID") UUID userId
	);

	@Operation(summary = "리뷰 댓글 목록 조회", description = "특정 리뷰에 달린 댓글 목록을 커서 기반 페이지네이션으로 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = CursorPageResponseCommentDto.class),
				examples = @ExampleObject(value = """
					{
					  "content": [
					    {
					      "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
					      "reviewId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
					      "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
					      "userNickname": "string",
					      "content": "string",
					      "createdAt": "2025-06-10T04:12:32.273Z",
					      "updatedAt": "2025-06-10T04:12:32.273Z"
					    }
					  ],
					  "nextCursor": "string",
					  "nextAfter": "2025-06-10T15:04:05.000Z",
					  "size": 10,
					  "totalElements": 100,
					  "hasNext": true
					}
					"""))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 (리뷰 ID 누락 등)"),
		@ApiResponse(responseCode = "404", description = "리뷰 정보 없음"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	ResponseEntity<CursorPageResponseCommentDto> getCommentList(
		@Parameter(description = "댓글 목록을 조회할 리뷰의 ID", required = true) @RequestParam UUID reviewId,
		@Parameter(description = "정렬 방향", schema = @Schema(defaultValue = "DESC", allowableValues = {
			"ASC", "DESC"})) @RequestParam(defaultValue = "DESC") String direction,
		@Parameter(description = "다음 페이지 커서로 사용할 이전 페이지 마지막 댓글의 ID") @RequestParam(required = false) UUID cursor,
		@Parameter(description = "다음 페이지 커서로 사용할 이전 페이지 마지막 댓글의 생성 시간 (ISO 8601 형식)") @RequestParam(required = false) LocalDateTime after,
		@Parameter(description = "한 페이지에 보여줄 댓글 수", schema = @Schema(defaultValue = "50")) @RequestParam(defaultValue = "50") int limit
	);

	@Operation(summary = "댓글 상세 정보 조회", description = "특정 댓글의 상세 정보를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "댓글 조회 성공",
			content = @Content(schema = @Schema(implementation = CommentDto.class))),
		@ApiResponse(responseCode = "404", description = "댓글 정보 없음"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	ResponseEntity<CommentDto> getComment(
		@Parameter(description = "조회할 댓글의 ID", required = true) @PathVariable UUID commentId
	);
}
