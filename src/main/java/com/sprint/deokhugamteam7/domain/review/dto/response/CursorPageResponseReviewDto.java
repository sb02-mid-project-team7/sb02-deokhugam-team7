package com.sprint.deokhugamteam7.domain.review.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record CursorPageResponseReviewDto(
    List<ReviewDto> content,
    String nextCursor,
    LocalDateTime nextAfter,
    int size,
    long totalElements,
    boolean hasNext
) {

}
