package com.sprint.deokhugamteam7.domain.review.scheduler;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.review.entity.RankingReview;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.review.repository.RankingReviewRepository;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewRepository;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewRepositoryCustom;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import com.sprint.deokhugamteam7.exception.review.ReviewException;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PopularReviewScoreSchedule {

  private final ReviewRepository reviewRepository;
  private final ReviewRepositoryCustom reviewRepositoryCustom;
  private final RankingReviewRepository rankingReviewRepository;

  @Scheduled(cron = "0 0/1 * * * *")
  //@Scheduled(cron = "0 0 0 * * *")
  public void scheduleScore() {
    //LocalDateTime end = LocalDate.now().atStartOfDay();
    LocalDateTime end = LocalDateTime.now();
    calculateReviewScore(end.minusDays(1), end, Period.DAILY);
    calculateReviewScore(end.minusWeeks(1), end, Period.WEEKLY);
    calculateReviewScore(end.minusMonths(1), end, Period.MONTHLY);
    calculateReviewScore(null, null, Period.ALL_TIME);
  }

  public void calculateReviewScore
      (@Nullable LocalDateTime start, @Nullable LocalDateTime end, Period period) {
    log.info("[PopularReviewScoreSchedule] 인기 유저 점수 계산 시작: period={}, start={}", period, start);

    Map<UUID, Long> likeMap = reviewRepositoryCustom.findLikeCountsByPeriod(start, end);
    Map<UUID, Long> commentMap = reviewRepositoryCustom.findCommentCountsByPeriod(start, end);

    Set<UUID> reviewIds = new HashSet<>();
    reviewIds.addAll(likeMap.keySet());
    reviewIds.addAll(commentMap.keySet());

    for (UUID id : reviewIds) {
      long likes = likeMap.getOrDefault(id, 0L);
      long comments = commentMap.getOrDefault(id, 0L);

      double score = Math.round(((likes * 0.3) + (comments * 0.7)) * 1000.0) / 1000.0;

      Review review = reviewRepository.findById(id)
          .orElseThrow(() -> new ReviewException(ErrorCode.INTERNAL_SERVER_ERROR));
      RankingReview ranking = rankingReviewRepository.findByReviewAndPeriod(review, period)
          .orElseGet(() -> RankingReview.create(review, score, period));

      ranking.update(score);
      rankingReviewRepository.save(ranking);
    }
  }
}
