package com.sprint.deokhugamteam7.domain.review.dto.response;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.review.entity.RankingReview;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record PopularReviewDto(
    UUID id,
    UUID reviewId,
    UUID bookId,
    String bookTitle,
    String bookThumbnailUrl,
    UUID userId,
    String userNickname,
    String reviewContent,
    double reviewRating,
    Period period,
    LocalDateTime createdAt,
    long rank,
    double score,
    long likeCount,
    long commentCount
) {

  public static PopularReviewDto of(
      RankingReview rankingReview, Review review, long rank, long likeCount, long commentCount) {
    return PopularReviewDto.builder()
        .id(rankingReview.getId())
        .reviewId(review.getId())
        .bookId(review.getBook().getId())
        .bookThumbnailUrl(review.getBook().getThumbnailUrl())
        .userId(review.getUser().getId())
        .userNickname(review.getUser().getNickname())
        .reviewContent(review.getContent())
        .reviewRating(review.getRating())
        .period(rankingReview.getPeriod())
        .createdAt(review.getCreatedAt())
        .rank(rank)
        .score(rankingReview.getScore())
        .likeCount(likeCount)
        .commentCount(commentCount)
        .build();
  }
}
