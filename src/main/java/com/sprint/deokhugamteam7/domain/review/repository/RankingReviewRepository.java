package com.sprint.deokhugamteam7.domain.review.repository;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.review.entity.RankingReview;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankingReviewRepository extends JpaRepository<RankingReview, UUID> {

  Optional<RankingReview> findByReviewAndPeriod(Review review, Period period);
}
