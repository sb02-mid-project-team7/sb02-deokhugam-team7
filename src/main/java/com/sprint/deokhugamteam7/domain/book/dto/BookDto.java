package com.sprint.deokhugamteam7.domain.book.dto;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.entity.RankingBook;
import com.sprint.deokhugamteam7.exception.DeokhugamException;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record BookDto(
    UUID id,
    String title,
    String author,
    String description,
    String publisher,
    LocalDate publishedDate,
    String isbn,
    String thumbnailUrl,
    Long reviewCount,
    Double rating,
    LocalDateTime createdAt,
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

  public static BookDto from(RankingBook rankingBook) {
    Book book = rankingBook.getBook();
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

  public static BookDto from(FindBookDto book) {
    return BookDto.builder()
        .id(book.id())
        .title(book.title())
        .author(book.author())
        .description(book.description())
        .publisher(book.publisher())
        .publishedDate(book.publishedDate())
        .isbn(book.isbn())
        .thumbnailUrl(book.thumbnailUrl())
        .createdAt(book.createdAt())
        .updatedAt(book.updatedAt())
        .reviewCount(book.reviewCount())
        .rating(book.rating()).build();
  }
}
