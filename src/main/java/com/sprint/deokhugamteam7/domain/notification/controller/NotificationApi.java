package com.sprint.deokhugamteam7.domain.notification.controller;

import com.sprint.deokhugamteam7.domain.notification.dto.CursorPageResponseNotificationDto;
import com.sprint.deokhugamteam7.domain.notification.dto.NotificationCursorRequest;
import com.sprint.deokhugamteam7.domain.notification.dto.NotificationDto;
import com.sprint.deokhugamteam7.domain.notification.dto.NotificationUpdateRequest;
import com.sprint.deokhugamteam7.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;

@Tag(name = "알림 관리", description = "알림 관련 API")
public interface NotificationApi {

  @Operation(summary = "알림 읽음 상태 업데이트")
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "204",
      description = "알림 상태 업데이트 성공",
      content = @Content(schema = @Schema(implementation = NotificationDto.class))
    ),
    @ApiResponse(
      responseCode = "400",
      description = "잘못된 요청 (입력값 검증 실패, 요청자 ID 누락)",
      content = @Content(
        schema = @Schema(implementation = ErrorResponse.class),
        examples = @ExampleObject(
          value = """
            {
                "timestamp": "2025-06-09T10:19:36.8086316",
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
      responseCode = "403",
      description = "알림 수정 권한 없음",
      content = @Content(
        schema = @Schema(implementation = ErrorResponse.class),
        examples = @ExampleObject(
          value = """
            {
              "timestamp": "2025-06-09T10:08:21.8756785",
              "code": "NOTIFICATION_NOT_OWNED",
              "message": "본인의 알람만 조회/수정할 수 있습니다.",
              "details": {},
              "exceptionType": "NotificationException",
              "status": 404
            }
            """
        )
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "알림 정보 없음",
      content = @Content(
        schema = @Schema(implementation = ErrorResponse.class),
        examples = @ExampleObject(
          value = """
            {
              "timestamp": "2025-06-09T10:08:21.8756785",
              "code": "NOTIFICATION_NOT_FOUND",
              "message": "알림을 찾을 수 없습니다.",
              "details": {},
              "exceptionType": "NotificationException",
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
              "timestamp": "2025-06-09T10:08:21.8756785",
              "code": "INTERNAL_SERVER_ERROR",
              "message": "서버 내부 오류",
              "details": {},
              "exceptionType": "Exception",
              "status": 500
            }
            """
        )
      )
    )
  })
  ResponseEntity<NotificationDto> update(
    @Parameter(
      description = "알림 ID",
      required = true,
      example = "123e4567-e89b-12d3-a456-426614174000"
    )
    UUID notificationId,
    @Parameter
    NotificationUpdateRequest request,
    @Parameter(
      description = "요청자 ID",
      required = true,
      example = "123e4567-e89b-12d3-a456-426614174000",
      name = "Deokhugam-Request-User-ID"
    )
    UUID userId
  );

  @Operation(summary = "모든 알림 읽음 처리")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "알림 읽음 처리 성공"),
    @ApiResponse(responseCode = "400", description = "잘못된 요청 (사용자 ID 누락)"),
    @ApiResponse(responseCode = "404", description = "사용자 정보 없음"),
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  ResponseEntity<Void> updateAll(
    @Parameter(
      description = "요청자 ID",
      required = true,
      example = "123e4567-e89b-12d3-a456-426614174000",
      name = "Deokhugam-Request-User-ID"
    )
    UUID userId
  );

  @Operation(summary = "알림 목록 조회")
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "알림 목록 조회 성공",
      content = @Content(schema = @Schema(implementation = CursorPageResponseNotificationDto.class))
    ),
    @ApiResponse(
      responseCode = "400",
      description = "잘못된 요청 (정렬 방향 오류, 페이지네이션 파라미터 오류, 사용자 ID 누락)",
      content = @Content(
        schema = @Schema(implementation = ErrorResponse.class),
        examples = @ExampleObject(
          value = """
            {
                "timestamp": "2025-06-09T10:31:44.2145952",
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
      description = "알림 정보 없음",
      content = @Content(
        schema = @Schema(implementation = ErrorResponse.class),
        examples = @ExampleObject(
          value = """
            {
              "timestamp": "2025-06-09T10:08:21.8756785",
              "code": "NOTIFICATION_NOT_FOUND",
              "message": "알림을 찾을 수 없습니다.",
              "details": {},
              "exceptionType": "NotificationException",
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
              "timestamp": "2025-06-09T10:08:21.8756785",
              "code": "INTERNAL_SERVER_ERROR",
              "message": "서버 내부 오류",
              "details": {},
              "exceptionType": "Exception",
              "status": 500
            }
            """
        )
      )
    )
  })
  ResponseEntity<CursorPageResponseNotificationDto> findAll(
    @Parameter(description = "페이징 커서 및 사용자 ID를 포함한 요청 파라미터", required = true)
    @ParameterObject NotificationCursorRequest request
  );
}
