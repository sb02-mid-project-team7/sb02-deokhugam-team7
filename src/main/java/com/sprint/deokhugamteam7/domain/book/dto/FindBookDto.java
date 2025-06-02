package com.sprint.deokhugamteam7.domain.book.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record FindBookDto(
    UUID id,
    String title,
    String author,
    String description,
    String publisher,
    LocalDate publishedDate,
    String isbn,
    String thumbnailUrl,
    long reviewCount,
    double rating,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

}