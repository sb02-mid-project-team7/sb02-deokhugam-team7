package com.sprint.deokhugamteam7.domain.review.dto.response;

import com.sprint.deokhugamteam7.domain.review.entity.Review;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ReviewDto(
    UUID id,
    UUID bookId,
    String bookTitle,
    String bookThumbnailUrl,
    UUID userId,
    String userNickname,
    String content,
    int rating,
    int likeCount,
    int commentCount,
    boolean likedByMe,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

  public static ReviewDto of(Review review, int likeCount, int commentCount,
      boolean likedByMe) {
    return ReviewDto.builder()
        .id(review.getId())
        .bookId(review.getBook().getId())
        .bookTitle(review.getBook().getTitle())
        .bookThumbnailUrl(review.getBook().getThumbnailUrl())
        .userId(review.getUser().getId())
        .userNickname(review.getUser().getNickname())
        .content(review.getContent())
        .rating(review.getRating())
        .likeCount(likeCount)
        .commentCount(commentCount)
        .likedByMe(likedByMe)
        .createdAt(review.getCreatedAt())
        .updatedAt(review.getUpdatedAt())
        .build();
  }
}
