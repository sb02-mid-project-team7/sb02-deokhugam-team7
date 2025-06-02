package com.sprint.deokhugamteam7.domain.comment.dto.request;

import java.util.UUID;
import lombok.Data;

@Data
public class CommentCreateRequest {

  private UUID reviewId;
  private UUID userId;
  private String content;
}
