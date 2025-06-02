package com.sprint.deokhugamteam7.domain.notification.dto;

import java.util.List;
import org.springframework.data.domain.Slice;

public record CursorPageResponseNotificationDto(
    List<NotificationDto> content,
    String nextCursor,
    String nextAfter,
    int size,
    long totalElements,
    boolean hasNext
) {
    public static CursorPageResponseNotificationDto fromSlice(
        Slice<NotificationDto> slice,
        long totalElements
    ) {
        String nextCursor = null;
        String nextAfter = null;
        if (slice.hasNext() && !slice.getContent().isEmpty()) {
            NotificationDto last = slice.getContent().get(slice.getContent().size() - 1);
            nextCursor = last.createdAt().toString();
            nextAfter = last.createdAt().toString();
        }

        return new CursorPageResponseNotificationDto(
            slice.getContent(),
            nextCursor,
            nextAfter,
            slice.getPageable().getPageSize(),
            totalElements,
            slice.hasNext()
        );
    }

}
