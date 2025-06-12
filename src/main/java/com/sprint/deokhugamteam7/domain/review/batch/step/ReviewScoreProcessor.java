package com.sprint.deokhugamteam7.domain.review.batch.step;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.review.dto.ReviewActivity;
import com.sprint.deokhugamteam7.domain.review.entity.RankingReview;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewRepository;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import com.sprint.deokhugamteam7.exception.review.ReviewException;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class ReviewScoreProcessor implements ItemProcessor<ReviewActivity, RankingReview> {

  private final ReviewRepository reviewRepository;
  private final Period period;

  public ReviewScoreProcessor(
      ReviewRepository reviewRepository,
      @Value("#{jobParameters['period']}") String periodStr
  ) {
    this.reviewRepository = reviewRepository;
    this.period = Period.valueOf(periodStr.toUpperCase());
  }

  @Override
  public RankingReview process(ReviewActivity reviewActivity) {
    Review review = reviewRepository.findById(reviewActivity.reviewId())
        .orElseThrow(() -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND));
    long likes = reviewActivity.likeCount();
    long comments = reviewActivity.commentCount();

    double score = Math.round(((likes * 0.3) + (comments * 0.7)) * 1000.0) / 1000.0;

    return RankingReview.create(review, score, period);
  }
}
