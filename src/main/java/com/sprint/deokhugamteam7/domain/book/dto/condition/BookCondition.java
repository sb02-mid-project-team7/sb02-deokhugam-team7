package com.sprint.deokhugamteam7.domain.book.dto.condition;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookCondition {

  String keyword;
  String cursor;
  LocalDateTime after;
  String orderBy = "title";
  String direction = "desc";
  int limit = 50;
}
