package com.sprint.deokhugamteam7.domain.review.dto.response;

import java.time.Instant;
import java.util.List;

public record CursorPageResponsePopularReviewDto(
    List<PopularReviewDto> content,
    String nextCursor,
    Instant nextAfter,
    int size,
    long totalElements,
    boolean hasNext
) {

}
