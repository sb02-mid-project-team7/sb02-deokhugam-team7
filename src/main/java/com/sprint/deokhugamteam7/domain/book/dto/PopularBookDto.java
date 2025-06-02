package com.sprint.deokhugamteam7.domain.book.dto;


import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record PopularBookDto(
    UUID id,
    UUID bookId,
    LocalDateTime createdAt,
    String title,
    String author,
    String thumbnailUrl,
    String period,
    long rank,
    double score,
    long reviewCount,
    double rating
) {

  public static PopularBookDto from(FindPopularBookDto bookDto, int rank) {
    return PopularBookDto.builder()
        .id(bookDto.id())
        .bookId(bookDto.bookId())
        .createdAt(bookDto.createdAt())
        .title(bookDto.title())
        .author(bookDto.author())
        .thumbnailUrl(bookDto.thumbnailUrl())
        .period(bookDto.period())
        .rank(rank)
        .score(bookDto.score())
        .reviewCount(bookDto.reviewCount())
        .rating(bookDto.rating())
        .build();
  }
}
