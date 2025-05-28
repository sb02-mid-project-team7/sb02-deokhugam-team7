package com.sprint.deokhugamteam7.domain.book.dto.response;


import java.time.LocalDate;
import java.util.UUID;

public record PopularBookDto(
    UUID id,
    UUID bookId,
    String title,
    String author,
    String thumbnailUrl,
    long rank,
    double score,
    long reviewCount,
    double rating,
    LocalDate createdAt
) {

}
