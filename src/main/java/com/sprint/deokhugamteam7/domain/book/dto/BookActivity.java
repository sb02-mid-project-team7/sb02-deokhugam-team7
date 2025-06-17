package com.sprint.deokhugamteam7.domain.book.dto;

import java.util.UUID;

public record BookActivity(
    UUID bookId,
    long reviewCount,
    int totalRating
) {
}
