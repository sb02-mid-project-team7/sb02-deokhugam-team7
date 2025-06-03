package com.sprint.deokhugamteam7.domain.review.dto.request;

import com.sprint.deokhugamteam7.constant.Period;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RankingReviewRequest {

  private Period period = Period.DAILY;
  private String direction = "ASC";
  private String cursor;
  LocalDateTime after;
  int limit = 50;
}
