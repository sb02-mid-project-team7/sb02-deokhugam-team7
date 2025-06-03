package com.sprint.deokhugamteam7.domain.user.dto;

import com.sprint.deokhugamteam7.constant.Period;
import java.time.LocalDateTime;
import org.springframework.data.domain.Sort;

public record PowerUserSearchCondition(
    Period period,
    String cursor,
    LocalDateTime after,
    int size,
    Sort.Direction direction
) {
  public Double parsedCursorScore() {
    return cursor != null ? Double.parseDouble(cursor) : null;
  }
}
