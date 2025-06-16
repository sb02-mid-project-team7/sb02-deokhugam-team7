package com.sprint.deokhugamteam7.domain.book.dto;

import java.util.UUID;

public record BookActivity(
    UUID bookId,
    int reviewCount,
    int totalRating
) {
}
