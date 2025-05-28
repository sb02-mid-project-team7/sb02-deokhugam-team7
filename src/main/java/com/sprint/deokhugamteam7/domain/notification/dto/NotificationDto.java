package com.sprint.deokhugamteam7.domain.notification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationDto(
    UUID id,
    UUID userId,
    UUID reviewId,
    String reviewTitle,
    String content,
    boolean confirmed,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

}
