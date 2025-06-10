package com.sprint.deokhugamteam7.domain.book.dto;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.entity.RankingBook;
import com.sprint.deokhugamteam7.exception.DeokhugamException;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "도서 응답 DTO")
public record BookDto(
    @Schema(description = "도서 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,
    @Schema(description = "도서 제목", example = "데미안")
    String title,
    @Schema(description = "작가", example = "헤르만 허세")
    String author,
    @Schema(description = "설명", example = "한 청년의 자아를 찾아가는 이야기")
    String description,
    @Schema(description = "출판사", example = "을유문화사")
    String publisher,
    @Schema(description = "출판일", example = "2023-05-10", type = "string", format = "date")
    LocalDate publishedDate,
    @Schema(description = "isbn", example = "9788990982575")
    String isbn,
    @Schema(description = "썸네일 이미지 URL", example = "https://example.com/thumbnail.jpg")
    String thumbnailUrl,
    @Schema(description = "리뷰 수", example = "12")
    Long reviewCount,
    @Schema(description = "평균 평점", example = "3.5")
    Double rating,
    @Schema(description = "생성일시", example = "2025-06-10T13:00:00", type = "string", format = "date-time")
    LocalDateTime createdAt,
    @Schema(description = "수정일시", example = "2025-06-10T13:00:00", type = "string", format = "date-time")
    LocalDateTime updatedAt
) {

  public static BookDto from(Book book) {
    RankingBook rankingBook = book.getRankingBooks().stream()
        .filter(rb -> rb.getPeriod().equals(Period.ALL_TIME)).findFirst()
        .orElseThrow(
            () -> new DeokhugamException(ErrorCode.INTERNAL_SERVER_ERROR)
        );
    return BookDto.builder()
        .id(book.getId())
        .title(book.getTitle())
        .author(book.getAuthor())
        .description(book.getDescription())
        .publisher(book.getPublisher())
        .publishedDate(book.getPublishedDate())
        .isbn(book.getIsbn())
        .thumbnailUrl(book.getThumbnailUrl())
        .createdAt(book.getCreatedAt())
        .updatedAt(book.getUpdatedAt())
        .reviewCount(rankingBook.getReviewCount())
        .rating(rankingBook.getRating()).build();
  }
}
