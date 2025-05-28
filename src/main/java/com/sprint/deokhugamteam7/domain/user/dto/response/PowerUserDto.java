package com.sprint.deokhugamteam7.domain.user.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record PowerUserDto(
    UUID userId,
    String nickname,
    String period,
    LocalDateTime createAt,
    long rank,
    double score,
    double reviewScoreSum,
    long likeCount,
    long commentCount
) {

}
