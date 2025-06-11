package com.sprint.deokhugamteam7.domain.review.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewSearchCondition {

  @Schema(
      description = "작성자 ID",
      example = "123e4567-e89b-12d3-a456-426614174000"
  )
  private UUID userId;

  @Schema(
      description = "도서 ID",
      example = "123e4567-e89b-12d3-a456-426614174000"
  )
  private UUID bookId;

  @Schema(
      description = "검색 키워드 (작성자 닉네임 | 내용)",
      example = "홍길동"
  )
  private String keyword;

  @Schema(
      description = "정렬 기준 (createdAt | rating)",
      example = "createdAt",
      defaultValue = "createdAt",
      allowableValues = {"createdAt", "rating"}
  )
  private String orderBy = "createdAt";

  @Schema(
      description = "정렬 방향",
      example = "DESC",
      defaultValue = "DESC",
      allowableValues = {"ASC", "DESC"}
  )
  private String direction = "DESC";

  @Schema(description = "커서 페이지네이션 커서")
  private String cursor;

  @Schema(description = "보조 커서(createdAt)")
  private LocalDateTime after;

  @Schema(
      description = "페이지 크기",
      example = "50",
      defaultValue = "50"
  )
  private int limit = 50;

  @Parameter(
      description = "요청자 ID",
      required = true,
      example = "123e4567-e89b-12d3-a456-426614174000"
  )
  private UUID requestUserId;
}
