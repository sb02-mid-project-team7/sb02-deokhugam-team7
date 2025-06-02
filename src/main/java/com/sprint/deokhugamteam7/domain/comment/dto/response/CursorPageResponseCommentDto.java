package com.sprint.deokhugamteam7.domain.comment.dto.response;

import com.sprint.deokhugamteam7.domain.comment.dto.CommentDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class CursorPageResponseCommentDto {

  private List<CommentDto> content;
  private String nextCursor; // 시간 기준 .
  private LocalDateTime nextAfter;
  private int size;
  private Long totalElements;
  private boolean hasNext;
}
