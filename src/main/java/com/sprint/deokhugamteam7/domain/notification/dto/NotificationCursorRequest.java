package com.sprint.deokhugamteam7.domain.notification.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.boot.context.properties.bind.DefaultValue;

public record NotificationCursorRequest(
    @NotNull
    UUID userId,
    @DefaultValue(value = "DESC")
    String direction,
    String cursor,
    LocalDateTime after,
    @DefaultValue(value = "20")
    Integer limit
) {
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