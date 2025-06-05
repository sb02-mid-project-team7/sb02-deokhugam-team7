package com.sprint.deokhugamteam7.domain.book.dto.response;

import com.sprint.deokhugamteam7.domain.book.dto.BookDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record CursorPageResponseBookDto(
    List<BookDto> content,
    String nextCursor,
    LocalDateTime nextAfter,
    int size,
    Long totalElements,
    boolean hasNext
) {

  public static CursorPageResponseBookDto of(List<BookDto> content,String orderBy ,int limit) {
    boolean hasNext = content.size() > limit;
    List<BookDto> page = hasNext ? content.subList(0, limit) : content;
    String nextCursor = null;
    LocalDateTime nextAfter = null;
    if (hasNext) {
      BookDto last = page.get(page.size() - 1);

      switch (orderBy) {
        case "publishedDate":
          nextCursor = last.publishedDate().toString();
          break;
        case "rating":
          nextCursor = String.valueOf(last.rating());
          break;
        case "reviewCount":
          nextCursor = String.valueOf(last.reviewCount());
          break;
        case "title":
        default:
          nextCursor = last.title();
          break;
      }
      nextAfter = last.createdAt();
    }

    return CursorPageResponseBookDto.builder()
        .content(page)
        .size(page.size())
        .hasNext(hasNext)
        .nextCursor(nextCursor)
        .nextAfter(nextAfter)
        .totalElements((long)content.size())
        .build();
  }
}
