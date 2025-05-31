package com.sprint.deokhugamteam7.domain.book.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Slice;

@Builder
public record CursorPageResponsePopularBookDto(
    List<PopularBookDto> content,
    String nextCursor,
    LocalDateTime nextAfter,
    int size,
    Long totalElements,
    boolean hasNext
) {

  public static CursorPageResponsePopularBookDto from(Slice<PopularBookDto> slice,
      String nextCursor) {
    List<PopularBookDto> content = slice.getContent();
    LocalDateTime nextAfter =
        content.isEmpty() ? null : content.get(content.size() - 1).createdAt();
    return CursorPageResponsePopularBookDto.builder()
        .content(content)
        .size(slice.getSize())
        .hasNext(slice.hasNext())
        .nextCursor(nextCursor)
        .nextAfter(nextAfter)
        .totalElements(slice.stream().count())
        .build();
  }

}
