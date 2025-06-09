package com.sprint.deokhugamteam7.domain.review.controller;

import com.sprint.deokhugamteam7.domain.review.dto.request.RankingReviewRequest;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewCreateRequest;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewSearchCondition;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewUpdateRequest;
import com.sprint.deokhugamteam7.domain.review.dto.response.CursorPageResponsePopularReviewDto;
import com.sprint.deokhugamteam7.domain.review.dto.response.CursorPageResponseReviewDto;
import com.sprint.deokhugamteam7.domain.review.dto.response.ReviewDto;
import com.sprint.deokhugamteam7.domain.review.dto.response.ReviewLikeDto;
import com.sprint.deokhugamteam7.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "리뷰 관리", description = "리뷰 관련 API")
public interface ReviewApi {

  @Operation(summary = "리뷰 등록", description = "새로운 리뷰를 등록합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201",
          description = "리뷰 등록 성공",
          content = @Content(schema = @Schema(implementation = ReviewDto.class))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "잘못된 요청 (입력값 검증 실패)",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "timestamp": "2025-06-09T15:22:24.5323466",
                        "code": "METHOD_ARGUMENT_NOT_VALID",
                        "message": "@Valid 유효성 검사에 실패했습니다.",
                        "details": {},
                        "exceptionType": "MethodArgumentNotValidException",
                        "status": 400
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "도서 정보 없음",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "timestamp": "2025-06-09T15:22:24.5323466",
                        "code": "BOOK_NOT_FOUND",
                        "message": "도서를 찾을 수 없습니다.",
                        "details": {},
                        "exceptionType": "ReviewException",
                        "status": 404
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "409",
          description = "이미 작성된 리뷰 존재",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "timestamp": "2025-06-09T15:22:24.5323466",
                        "code": "REVIEW_ALREADY_EXISTS",
                        "message": "이미 해당 도서에 대한 리뷰가 존재합니다.",
                        "details": {},
                        "exceptionType": "ReviewException",
                        "status": 409
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "서버 내부 오류",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "timestamp": "2025-06-09T15:22:24.5323466",
                        "code": "INTERNAL_SERVER_ERROR",
                        "message": "서버 내부 오류",
                        "details": {},
                        "exceptionType": "ReviewException",
                        "status": 500
                      }
                      """
              )
          )
      )
  })
  ResponseEntity<ReviewDto> create(
      @Parameter(
          description = "리뷰 정보",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ReviewCreateRequest.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "bookId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                        "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                        "content": "string",
                        "rating": 1
                      }
                      """
              )
          )
      )
      ReviewCreateRequest request
  );

  @Operation(summary = "리뷰 수정", description = "본인이 작성한 리뷰를 수정합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "리뷰 수정 성공",
          content = @Content(schema = @Schema(implementation = ReviewDto.class))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "잘못된 요청 (입력값 검증 실패)",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "timestamp": "2025-06-09T15:22:24.5323466",
                        "code": "METHOD_ARGUMENT_NOT_VALID",
                        "message": "@Valid 유효성 검사에 실패했습니다.",
                        "details": {},
                        "exceptionType": "MethodArgumentNotValidException",
                        "status": 400
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "403",
          description = "리뷰 수정 권한 없음",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "timestamp": "2025-06-09T15:22:24.5323466",
                        "code": "REVIEW_NOT_OWNED",
                        "message": "자신의 리뷰만 수정/삭제할 수 있습니다.",
                        "details": {},
                        "exceptionType": "ReviewException",
                        "status": 403
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "리뷰 정보 없음",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "timestamp": "2025-06-09T15:22:24.5323466",
                        "code": "REVIEW_NOT_FOUND",
                        "message": "리뷰를 찾을 수 없습니다.",
                        "details": {},
                        "exceptionType": "ReviewException",
                        "status": 404
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "서버 내부 오류",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "timestamp": "2025-06-09T15:22:24.5323466",
                        "code": "INTERNAL_SERVER_ERROR",
                        "message": "서버 내부 오류",
                        "details": {},
                        "exceptionType": "ReviewException",
                        "status": 500
                      }
                      """
              )
          )
      )
  })
  ResponseEntity<ReviewDto> update(
      @Parameter(
          description = "리뷰 ID",
          required = true,
          example = "123e4567-e89b-12d3-a456-426614174000"
      )
      UUID reviewId,
      @Parameter(
          description = "요청자 ID",
          required = true,
          example = "123e4567-e89b-12d3-a456-426614174000",
          name = "Deokhugam-Request-User-ID"
      )
      UUID userId,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ReviewUpdateRequest.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "content": "string",
                        "rating": 1
                      }
                      """
              )
          )
      )
      ReviewUpdateRequest request
  );

  @Operation(summary = "리뷰 논리 삭제", description = "본인이 작성한 리뷰를 논리적으로 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "리뷰 논리적 삭제 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (요청자 ID 누락)"),
      @ApiResponse(responseCode = "403", description = "리뷰 삭제 권한 없음"),
      @ApiResponse(responseCode = "404", description = "리뷰 정보 없음"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  ResponseEntity<Void> deleteSoft(
      @Parameter(
          description = "리뷰 ID",
          required = true,
          example = "123e4567-e89b-12d3-a456-426614174000"
      )
      UUID reviewId,
      @Parameter(
          description = "요청자 ID",
          required = true,
          example = "123e4567-e89b-12d3-a456-426614174000",
          name = "Deokhugam-Request-User-ID"
      )
      UUID userId
  );

  @Operation(summary = "리뷰 물리 삭제", description = "본인이 작성한 리뷰를 물리적으로 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "리뷰 물리적 삭제 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (요청자 ID 누락)"),
      @ApiResponse(responseCode = "403", description = "리뷰 삭제 권한 없음"),
      @ApiResponse(responseCode = "404", description = "리뷰 정보 없음"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  ResponseEntity<Void> deleteHard(
      @Parameter(
          description = "리뷰 ID",
          required = true,
          example = "123e4567-e89b-12d3-a456-426614174000"
      )
      UUID reviewId,
      @Parameter(
          description = "요청자 ID",
          required = true,
          example = "123e4567-e89b-12d3-a456-426614174000",
          name = "Deokhugam-Request-User-ID"
      )
      UUID userId
  );

  @Operation(summary = "리뷰 상세 정보 조회", description = "리뷰 ID로 상세 정보를 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "리뷰 정보 조회 성공",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ReviewDto.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "잘못된 요청 (요청자 ID 누락)",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "timestamp": "2025-06-09T15:22:24.5323466",
                        "code": "METHOD_ARGUMENT_NOT_VALID",
                        "message": "@Valid 유효성 검사에 실패했습니다.",
                        "details": {},
                        "exceptionType": "MethodArgumentNotValidException",
                        "status": 400
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "리뷰 정보 없음",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "timestamp": "2025-06-09T15:22:24.5323466",
                        "code": "REVIEW_NOT_FOUND",
                        "message": "리뷰를 찾을 수 없습니다.",
                        "details": {},
                        "exceptionType": "ReviewException",
                        "status": 404
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "서버 내부 오류",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "timestamp": "2025-06-09T15:22:24.5323466",
                        "code": "INTERNAL_SERVER_ERROR",
                        "message": "서버 내부 오류",
                        "details": {},
                        "exceptionType": "ReviewException",
                        "status": 500
                      }
                      """
              )
          )
      )
  })
  ResponseEntity<ReviewDto> findById(
      @Parameter(
          description = "리뷰 ID",
          required = true,
          example = "123e4567-e89b-12d3-a456-426614174000"
      )
      UUID reviewId,
      @Parameter(
          description = "요청자 ID",
          required = true,
          example = "123e4567-e89b-12d3-a456-426614174000",
          name = "Deokhugam-Request-User-ID"
      )
      UUID userId
  );

  @Operation(summary = "리뷰 좋아요", description = "리뷰에 좋아요를 추가하거나 취소합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "리뷰 좋아요 성공",
          content = @Content(schema = @Schema(implementation = ReviewLikeDto.class))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "잘못된 요청 (요청자 ID 누락)",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "timestamp": "2025-06-09T15:22:24.5323466",
                        "code": "MISSING_REQUEST_HEADER",
                        "message": "Request header가 누락되었습니다.",
                        "details": {},
                        "exceptionType": "ReviewException",
                        "status": 400
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "리뷰 정보 없음",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "timestamp": "2025-06-09T15:22:24.5323466",
                        "code": "REVIEW_NOT_FOUND",
                        "message": "리뷰를 찾을 수 없습니다.",
                        "details": {},
                        "exceptionType": "ReviewException",
                        "status": 404
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "서버 내부 오류",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "timestamp": "2025-06-09T15:22:24.5323466",
                        "code": "INTERNAL_SERVER_ERROR",
                        "message": "서버 내부 오류",
                        "details": {},
                        "exceptionType": "ReviewException",
                        "status": 500
                      }
                      """
              )
          )
      )
  })
  ResponseEntity<ReviewLikeDto> like(
      @Parameter(
          description = "리뷰 ID",
          required = true,
          example = "123e4567-e89b-12d3-a456-426614174000"
      )
      UUID reviewId,
      @Parameter(
          description = "요청자 ID",
          required = true,
          example = "123e4567-e89b-12d3-a456-426614174000",
          name = "Deokhugam-Request-User-ID"
      )
      UUID userId
  );

  @Operation(summary = "리뷰 목록 조회", description = "검색 조건에 맞는 리뷰 목록을 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "리뷰 목록 조회 성공",
          content = @Content(schema = @Schema(implementation = CursorPageResponseReviewDto.class))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "잘못된 요청 (정렬 기준 오류, 페이지네이션 파라미터 오류, 요청자 ID 누락)",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "timestamp": "2025-06-09T15:22:24.5323466",
                        "code": "METHOD_ARGUMENT_TYPE_MISMATCH",
                        "message": "요청 파라미터 타입이 잘못되었습니다.",
                        "details": {},
                        "exceptionType": "MethodArgumentTypeMismatchException",
                        "status": 400
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "서버 내부 오류",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "timestamp": "2025-06-09T15:22:24.5323466",
                        "code": "INTERNAL_SERVER_ERROR",
                        "message": "서버 내부 오류",
                        "details": {},
                        "exceptionType": "ReviewException",
                        "status": 500
                      }
                      """
              )
          )
      )
  })
  ResponseEntity<CursorPageResponseReviewDto> findAll(
      @ParameterObject ReviewSearchCondition condition,
      @Parameter(
          description = "요청자 ID",
          required = true,
          example = "123e4567-e89b-12d3-a456-426614174000",
          name = "Deokhugam-Request-User-ID"
      )
      UUID headerUserId
  );

  @Operation(summary = "인기 리뷰 목록 조회", description = "기간별 인기 리뷰 목록을 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "인기 리뷰 목록 조회 성공",
          content = @Content(schema = @Schema(implementation = CursorPageResponsePopularReviewDto.class))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "잘못된 요청 (랭킹 기간 오류, 정렬 방향 오류 등)",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "timestamp": "2025-06-09T15:22:24.5323466",
                        "code": "METHOD_ARGUMENT_TYPE_MISMATCH",
                        "message": "요청 파라미터 타입이 잘못되었습니다.",
                        "details": {},
                        "exceptionType": "MethodArgumentTypeMismatchException",
                        "status": 400
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "서버 내부 오류",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "timestamp": "2025-06-09T15:22:24.5323466",
                        "code": "INTERNAL_SERVER_ERROR",
                        "message": "서버 내부 오류",
                        "details": {},
                        "exceptionType": "ReviewException",
                        "status": 500
                      }
                      """
              )
          )
      )
  })
  ResponseEntity<CursorPageResponsePopularReviewDto> popular(
      @ParameterObject RankingReviewRequest request);
}