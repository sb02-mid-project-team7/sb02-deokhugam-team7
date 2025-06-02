package com.sprint.deokhugamteam7.domain.user.dto.response;

import com.sprint.deokhugamteam7.constant.Period;
import java.time.LocalDateTime;
import java.util.UUID;

public record PowerUserDto(
    UUID userId,
    String nickname,
    Period period,
    LocalDateTime createdAt,
    long rank,
    double score,
    double reviewScoreSum,
    long likeCount,
    long commentCount
) {

}
