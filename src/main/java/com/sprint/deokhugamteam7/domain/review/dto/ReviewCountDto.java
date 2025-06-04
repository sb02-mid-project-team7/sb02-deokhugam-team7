package com.sprint.deokhugamteam7.domain.review.dto;

import com.sprint.deokhugamteam7.domain.review.entity.Review;
import java.util.UUID;

public record ReviewCountDto(
    UUID reviewId,
    Long count,
    Review review
    ) {

}
