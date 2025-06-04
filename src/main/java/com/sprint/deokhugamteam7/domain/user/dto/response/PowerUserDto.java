package com.sprint.deokhugamteam7.domain.user.dto.response;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.user.entity.UserScore;
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

  public static PowerUserDto from(UserScore us) {
    return new PowerUserDto(
        us.getUser().getId(),
        us.getUser().getNickname(),
        us.getPeriod(),
        us.getCreatedAt(),
        us.getRank() != null ? us.getRank() : 0L,
        us.getScore(),
        us.getReviewScoreSum(),
        us.getLikeCount(),
        us.getCommentCount()
    );
  }
}