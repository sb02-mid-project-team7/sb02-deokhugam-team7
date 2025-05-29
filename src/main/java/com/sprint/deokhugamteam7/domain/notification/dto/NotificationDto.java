package com.sprint.deokhugamteam7.domain.notification.dto;

import com.sprint.deokhugamteam7.domain.notification.entity.Notification;
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
    public static NotificationDto fromEntity(Notification notification) {
        return new NotificationDto(
            notification.getId(),
            notification.getUser().getId(),
            notification.getReview().getId(),
            notification.getReview().getUser().getNickname(), // review 작성자의 닉네임이 title로 사용됨
            notification.getContent(),
            notification.getConfirmed(),
            notification.getCreated_at(),
            notification.getUpdated_at()
        );
    }
}
