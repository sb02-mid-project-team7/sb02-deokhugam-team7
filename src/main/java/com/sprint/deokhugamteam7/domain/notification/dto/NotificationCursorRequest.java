package com.sprint.deokhugamteam7.domain.notification.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationCursorRequest(
    @NotNull
    UUID userId,
    String direction,
    String cursor,
    LocalDateTime after,
    Integer limit
) {
    public NotificationCursorRequest {
        if (direction == null || direction.isBlank()) {
            direction = "DESC";
        }
        if (limit == null) {
            limit = 20;
        }
    }

    @AssertTrue(message = "cursor가 존재하면 after(보조 커서)도 반드시 존재해야 합니다.")
    public boolean isCursorAfterValid() {
        if (cursor != null && !cursor.isBlank()) {
            return after != null;
        }
        return true;
    }

    public LocalDateTime parsedCursor() {
        if (cursor == null || cursor.isBlank()) return null;
        return LocalDateTime.parse(cursor);
    }
}