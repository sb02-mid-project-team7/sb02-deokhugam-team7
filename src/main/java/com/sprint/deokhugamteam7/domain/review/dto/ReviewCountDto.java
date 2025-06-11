package com.sprint.deokhugamteam7.domain.review.dto;

import java.util.UUID;

public record ReviewCountDto(
    UUID reviewId,
    Long count
) {

}
