package com.sprint.deokhugamteam7.domain.review.dto.request;

import java.time.Instant;
import java.util.UUID;

public record ReviewSearchCondition(
    UUID userId,
    UUID bookId,
    String keyword,
    String orderBy,
    String direction,
    String cursor,
    Instant after,
    int limit
) {

}
