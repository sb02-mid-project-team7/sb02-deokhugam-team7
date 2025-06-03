package com.sprint.deokhugamteam7.domain.user.dto.response;

import com.sprint.deokhugamteam7.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
    UUID id,
    String email,
    String nickname,
    LocalDateTime createdAt
) {
  public static UserDto from(User user) {
    return new UserDto(
        user.getId(),
        user.getEmail(),
        user.getNickname(),
        user.getCreatedAt()
    );
  }
}
