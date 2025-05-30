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
//TODO nextCursor, nextAfter 구현해야함
  public static CursorPageResponseBookDto from(Slice<BookDto> slice) {
    return CursorPageResponseBookDto.builder()
        .content(slice.getContent())
        .size(slice.getSize())
        .hasNext(slice.hasNext())
        .totalElements(slice.stream().count())
        .build();
  }

}
