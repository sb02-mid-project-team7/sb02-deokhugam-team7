package com.sprint.deokhugamteam7.domain.book.dto.response;

import com.sprint.deokhugamteam7.domain.book.dto.BookDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Slice;

@Builder
public record CursorPageResponseBookDto(
    List<BookDto> content,
    String nextCursor,
    LocalDateTime nextAfter,
    int size,
    Long totalElements,
    boolean hasNext
) {

  public static CursorPageResponseBookDto of(Slice<BookDto> slice, String keyword) {
    List<BookDto> content = slice.getContent();
    LocalDateTime nextAfter =
        content.isEmpty() ? null : content.get(content.size() - 1).createdAt();
    return CursorPageResponseBookDto.builder()
        .content(content)
        .size(slice.getSize())
        .hasNext(slice.hasNext())
        .nextCursor(keyword) //
        .nextAfter(nextAfter)
        .totalElements(slice.stream().count())
        .build();
  }

}
