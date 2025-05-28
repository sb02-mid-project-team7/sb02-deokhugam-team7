package com.sprint.deokhugamteam7.domain.book.dto.response;

import com.sprint.deokhugamteam7.domain.book.dto.BookDto;
import java.time.LocalDateTime;
import java.util.List;

public record CursorPageResponseBookDto(
    List<BookDto> content,
    String nextCursor,
    LocalDateTime nextAfter,
    int size,
    Long totalElements,
    boolean hasNext
) {

}
