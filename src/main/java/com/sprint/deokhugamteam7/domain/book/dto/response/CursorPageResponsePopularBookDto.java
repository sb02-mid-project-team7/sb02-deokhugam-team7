package com.sprint.deokhugamteam7.domain.book.dto.response;

import com.sprint.deokhugamteam7.domain.book.dto.PopularBookDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record CursorPageResponsePopularBookDto(
    List<PopularBookDto> content,
    String nextCursor,
    LocalDateTime nextAfter,
    int size,
    Long totalElements,
    boolean hasNext
) {

  public static CursorPageResponsePopularBookDto of(List<PopularBookDto> content,int limit) {
    boolean hasNext = content.size() > limit;
    List<PopularBookDto> page = hasNext ? content.subList(0, limit) : content;
    String nextCursor = null;
    LocalDateTime nextAfter = null;
    if (hasNext) {
      PopularBookDto last = page.get(page.size() - 1);
      nextCursor = String.valueOf(last.score());
      nextAfter = last.createdAt();
    }

    return CursorPageResponsePopularBookDto.builder()
        .content(page)
        .size(page.size())
        .hasNext(hasNext)
        .nextCursor(nextCursor)
        .nextAfter(nextAfter)
        .totalElements((long)content.size())
        .build();
  }

}
