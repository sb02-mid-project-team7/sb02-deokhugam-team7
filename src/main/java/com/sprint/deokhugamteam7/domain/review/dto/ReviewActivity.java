package com.sprint.deokhugamteam7.domain.review.dto;

import java.util.UUID;

public record ReviewActivity(
    UUID reviewId,
    long likeCount,
    long commentCount
) {

}
