package com.sprint.deokhugamteam7.domain.book.dto;

import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
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
    int reviewCount,
    double rating,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
  public static BookDto from(Book book) {
    List<Review> reviews = book.getReviews();
    int reviewCount = 0;
    double rating = 0.0;
    if (reviews != null) {
      reviewCount = reviews.size();
      rating = reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0);
    }

    return BookDto.builder()
        .id(book.getId())
        .title(book.getTitle())
        .author(book.getAuthor())
        .description(book.getDescription())
        .publisher(book.getPublisher())
        .publishedDate(book.getPublisherDate())
        .isbn(book.getIsbn())
        .thumbnailUrl(book.getThumbnailUrl())
        .createdAt(book.getCreatedAt())
        .updatedAt(book.getUpdatedAt())
        .reviewCount(reviewCount)
        .rating(rating).build();
  }
}
