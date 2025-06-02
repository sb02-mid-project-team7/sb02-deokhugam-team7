package com.sprint.deokhugamteam7.domain.comment.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class CommentDto {

  private UUID id;
  private UUID reviewId;
  private UUID userId;
  private String userNickname;
  private String content;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
