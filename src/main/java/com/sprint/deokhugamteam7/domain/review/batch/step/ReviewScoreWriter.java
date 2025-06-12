package com.sprint.deokhugamteam7.domain.review.batch.step;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.review.entity.RankingReview;
import com.sprint.deokhugamteam7.domain.review.repository.RankingReviewRepository;
import com.sprint.deokhugamteam7.domain.review.repository.custom.ReviewRepositoryCustom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class ReviewScoreWriter implements ItemWriter<RankingReview> {

  private final RankingReviewRepository rankingReviewRepository;
  private final ReviewRepositoryCustom reviewRepositoryCustom;
  private final Period period;

  public ReviewScoreWriter(
      RankingReviewRepository rankingReviewRepository,
      ReviewRepositoryCustom reviewRepositoryCustom,
      @Value("#{jobParameters['period']}") String periodStr
  ) {
    this.rankingReviewRepository = rankingReviewRepository;
    this.reviewRepositoryCustom = reviewRepositoryCustom;
    this.period = Period.valueOf(periodStr.toUpperCase());
  }

  @Override
  public void write(Chunk<? extends RankingReview> chunk) {
    List<? extends RankingReview> list = chunk.getItems();

    Set<UUID> reviewIds = list.stream()
        .map(r -> r.getReview().getId()).collect(Collectors.toSet());
    Map<UUID, RankingReview> existingMap =
        reviewRepositoryCustom.findAllByReviewIdInAndPeriod(reviewIds, period);

    List<RankingReview> toSave = new ArrayList<>();

    for (RankingReview ranking : list) {
      UUID reviewId = ranking.getReview().getId();
      RankingReview existing = existingMap.get(reviewId);

      if (existing != null) {
        existing.update(ranking.getScore());
        toSave.add(existing);
      } else {
        toSave.add(ranking);
      }
    }

    rankingReviewRepository.saveAll(toSave);
  }
}
