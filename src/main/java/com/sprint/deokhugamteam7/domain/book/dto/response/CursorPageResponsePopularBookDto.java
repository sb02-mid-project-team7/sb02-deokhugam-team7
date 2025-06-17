package com.sprint.deokhugamteam7.domain.book.dto.response;

import com.sprint.deokhugamteam7.domain.book.dto.PopularBookDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
@Schema(description = "인기 도서 커서 기반 페이지네이션 응답")
public record CursorPageResponsePopularBookDto(
    @Schema(description = "인기 도서 목록")
    List<PopularBookDto> content,
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

  public static CursorPageResponsePopularBookDto of(List<PopularBookDto> content,int limit) {
    boolean hasNext = content.size() > limit;
    List<PopularBookDto> page = hasNext ? content.subList(0, limit) : content;
    String nextCursor = null;
    LocalDateTime nextAfter = null;
    if (hasNext) {
      PopularBookDto last = page.get(page.size() - 1);
      nextCursor = String.valueOf(last.rank());
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
