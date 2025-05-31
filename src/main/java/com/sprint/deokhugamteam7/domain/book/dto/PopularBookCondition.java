package com.sprint.deokhugamteam7.domain.book.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PopularBookCondition {
  String period = "DAILY";
  String cursor = "10";
  LocalDateTime after;
  String direction = "desc";
  int limit = 50;
}
