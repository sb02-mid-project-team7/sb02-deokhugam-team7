package com.sprint.deokhugamteam7.domain.user.dto;

import java.util.UUID;

public record UserActivity(
    UUID userId,
    double reviewScoreSum,
    long likeCount,
    long commentCount
) {
}