package com.sprint.deokhugamteam7.domain.notification.dto;

import java.util.List;

public record CursorPageResponseNotificationDto(
    List<NotificationDto> content,
    String nextCursor,
    String nextAfter,
    int size,
    long totalElements,
    boolean hasNext
) {

}
