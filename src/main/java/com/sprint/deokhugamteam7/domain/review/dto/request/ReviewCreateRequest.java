package com.sprint.deokhugamteam7.domain.review.dto.request;

import java.util.UUID;

public record ReviewCreateRequest(
    UUID bookId,
    UUID userId,
    String content,
    int rating
) {

}
