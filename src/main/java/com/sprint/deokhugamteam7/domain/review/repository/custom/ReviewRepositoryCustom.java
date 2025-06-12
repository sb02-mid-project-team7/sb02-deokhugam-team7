package com.sprint.deokhugamteam7.domain.review.repository.custom;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.review.dto.ReviewActivity;
import com.sprint.deokhugamteam7.domain.review.dto.request.RankingReviewRequest;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewSearchCondition;
import com.sprint.deokhugamteam7.domain.review.entity.RankingReview;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ReviewRepositoryCustom {

  List<Review> findAll(ReviewSearchCondition condition, int limit);

  long countByCondition(ReviewSearchCondition condition);

  List<RankingReview> findRankingReviewsByPeriod(RankingReviewRequest request, int limit);

  List<ReviewActivity> findReviewActivitySummary(LocalDateTime start, LocalDateTime end);

  Map<UUID, RankingReview> findAllByReviewIdInAndPeriod(Set<UUID> reviewIds, Period period);

  long countRakingReviewByPeriod(Period period);

  Map<UUID, Integer> countLikesByReviewIds(List<UUID> reviewIds);

  Map<UUID, Integer> countCommentsByReviewIds(List<UUID> reviewIds);
}
