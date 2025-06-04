package com.sprint.deokhugamteam7.domain.user.dto;

import com.sprint.deokhugamteam7.constant.Period;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
public class PowerUserSearchCondition {

  private Period period;
  private String cursor;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime after;

  private int size = 10; // default
  private Sort.Direction direction = Sort.Direction.DESC; // default

  public Double parsedCursorScore() {
    return cursor != null ? Double.parseDouble(cursor) : null;
  }
}
