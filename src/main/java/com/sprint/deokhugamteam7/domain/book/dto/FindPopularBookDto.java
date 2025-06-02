package com.sprint.deokhugamteam7.domain.book.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FindPopularBookDto(
    UUID id,
    UUID bookId,
    LocalDateTime createdAt,
    String title,
    String author,
    String thumbnailUrl,
    String period,
    double score,
    long reviewCount,
    double rating
) {

}
