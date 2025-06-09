package com.sprint.deokhugamteam7.domain.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationCursorRequest(
    @NotNull
    @Schema(description = "사용자 ID", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    UUID userId,

    @Schema(description = "정렬 방향", allowableValues = {"ASC", "DESC"}, defaultValue = "DESC", example = "DESC")
    String direction,

    @Schema(description = "커서 페이지 네이션 커서")
    String cursor,

    @Schema(description = "보조 커서(createdAt)")
    LocalDateTime after,

    @Schema(description = "페이지 크기", defaultValue = "20", example = "20")
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