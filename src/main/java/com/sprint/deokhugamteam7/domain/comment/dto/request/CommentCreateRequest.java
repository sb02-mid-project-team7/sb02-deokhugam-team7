package com.sprint.deokhugamteam7.domain.comment.dto.request;

import java.util.UUID;

public record CommentCreateRequest(
    UUID reviewId,
    UUID userId,
    String content
) {

}
