package com.sprint.deokhugamteam7.domain.book.dto.condition;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookCondition {
  @Schema(
      description = "검색 키워드 (도서 제목 | 저자 | ISBN)",
      example = "자바"
  )
  String keyword;

  @Schema(description = "커서 페이지네이션 커서")
  String cursor;

  @Schema(description = "보조 커서(createdAt)")
  LocalDateTime after;

  @Schema(
      description = "정렬 기준 (title | publishedDate | rating | reviewCount)",
      example = "title",
      defaultValue = "title",
      allowableValues = {"title", "rating", "reviewCount"}
  )
  String orderBy = "title";

  @Schema(
      description = "정렬 방향",
      example = "desc",
      defaultValue = "desc",
      allowableValues = {"asc", "desc"}
  )
  String direction = "desc";

  @Schema(
      description = "페이지 크기",
      example = "50",
      defaultValue = "50"
  )
  int limit = 50;
}
