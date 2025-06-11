package com.sprint.deokhugamteam7.domain.review.dto.request;

import com.sprint.deokhugamteam7.constant.Period;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class RankingReviewRequest {

  @Schema(
      description = "랭킹 기간",
      example = "DAILY",
      defaultValue = "DAILY",
      allowableValues = {"DAILY", "WEEKLY", "MONTHLY", "ALL_TIME"}
  )
  private Period period = Period.DAILY;

  @Schema(
      description = "정렬 방향",
      example = "ASC",
      defaultValue = "ASC",
      allowableValues = {"DESC", "ASC"}
  )
  private String direction = "ASC";

  @Schema(description = "커서 페이지네이션 커서")
  private String cursor;

  @Schema(description = "보조 커서(createdAt)")
  LocalDateTime after;

  @Schema(
      description = "페이지 크기",
      example = "50",
      defaultValue = "50"
  )
  int limit = 50;
}
