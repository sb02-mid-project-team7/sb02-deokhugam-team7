package com.sprint.deokhugamteam7.domain.review.dto.response;

import com.sprint.deokhugamteam7.constant.Period;
import java.time.Instant;
import java.util.UUID;

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
    Instant createdAt,
    int rank,
    double score,
    long likeCount,
    long commentCount
) {

}
