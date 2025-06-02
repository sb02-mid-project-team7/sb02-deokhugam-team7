package com.sprint.deokhugamteam7.domain.review.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewSearchCondition {

  private UUID userId;
  private UUID bookId;
  private String keyword;
  private String orderBy = "createdAt";
  private String direction = "DESC";
  private String cursor;
  private LocalDateTime after;
  private int limit = 50;
  private UUID requestUserId;
}
