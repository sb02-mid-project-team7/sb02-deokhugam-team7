package com.sprint.deokhugamteam7.domain.review.repository;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.review.dto.request.RankingReviewRequest;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewSearchCondition;
import com.sprint.deokhugamteam7.domain.review.entity.RankingReview;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ReviewRepositoryCustom {

  List<Review> findAll(ReviewSearchCondition condition, int limit);

  long countByCondition(ReviewSearchCondition condition);

  Map<UUID, Long> findLikeCountsByPeriod(LocalDateTime start, LocalDateTime end);

  Map<UUID, Long> findCommentCountsByPeriod(LocalDateTime start, LocalDateTime end);

  List<RankingReview> findRankingReviewsByPeriod(RankingReviewRequest request, int limit);

  long countRakingReviewByPeriod(Period period);
}
