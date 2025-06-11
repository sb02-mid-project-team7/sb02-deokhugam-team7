package com.sprint.deokhugamteam7.domain.book.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "인기 도서 응답 DTO")
public record PopularBookDto(
    @Schema(description = "인기 도서 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,
    @Schema(description = "도서 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID bookId,
    @Schema(description = "도서 생성일시", example = "2025-06-10T13:00:00", type = "string", format = "date-time")
    LocalDateTime createdAt,
    @Schema(description = "도서 제목", example = "데미안")
    String title,
    @Schema(description = "작가", example = "헤르만 허세")
    String author,
    @Schema(description = "썸네일 이미지 URL", example = "https://example.com/thumbnail.jpg")
    String thumbnailUrl,
    @Schema(description = "기간", example = "DAILY")
    String period,
    @Schema(description = "순위", example = "1")
    long rank,
    @Schema(description = "점수", example = "6.9")
    double score,
    @Schema(description = "리뷰 수", example = "12")
    long reviewCount,
    @Schema(description = "평균 평점", example = "3.5")
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
