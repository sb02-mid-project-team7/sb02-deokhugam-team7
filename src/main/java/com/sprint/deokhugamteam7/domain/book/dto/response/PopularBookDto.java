package com.sprint.deokhugamteam7.domain.book.dto.response;


import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.entity.RankingBook;
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

  public static PopularBookDto from(RankingBook rankingBook, int rank) {
    Book book = rankingBook.getBook();
    return PopularBookDto.builder()
        .id(rankingBook.getId())
        .bookId(book.getId())
        .createdAt(book.getCreatedAt())
        .title(book.getTitle())
        .author(book.getAuthor())
        .thumbnailUrl(book.getThumbnailUrl())
        .period(rankingBook.getPeriod().toString())
        .rank(rank)
        .score(rankingBook.getScore())
        .reviewCount(rankingBook.getReviewCount())
        .rating(rankingBook.getRating())
        .build();
  }
}
