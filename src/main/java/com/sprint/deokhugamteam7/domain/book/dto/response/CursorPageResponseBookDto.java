package com.sprint.deokhugamteam7.domain.book.dto.response;

import com.sprint.deokhugamteam7.domain.book.dto.BookDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
@Schema(description = "도서 커서 기반 페이지네이션 응답")
public record CursorPageResponseBookDto(
    @Schema(description = "도서 목록")
    List<BookDto> content,
    @Schema(description = "다음 페이지네이션 커서")
    String nextCursor,
    @Schema(description = "보조 커서(createdAt)", example = "2025-06-10T13:00:00", type = "string", format = "date-time")
    LocalDateTime nextAfter,
    @Schema(description = "페이지 크기", example = "10")
    int size,
    @Schema(description = "총 요소 수", example = "10")
    Long totalElements,
    @Schema(description = "다음 페이지 여부", example = "true")
    boolean hasNext
) {

  public static CursorPageResponseBookDto of(List<BookDto> content,String orderBy ,int limit) {
    boolean hasNext = content.size() > limit;
    List<BookDto> page = hasNext ? content.subList(0, limit) : content;
    String nextCursor = null;
    LocalDateTime nextAfter = null;
    if (hasNext) {
      BookDto last = page.get(page.size() - 1);

      nextCursor = switch (orderBy) {
        case "publishedDate" -> last.publishedDate().toString();
        case "rating" -> String.valueOf(last.rating());
        case "reviewCount" -> String.valueOf(last.reviewCount());
        default -> last.title();
      };

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
