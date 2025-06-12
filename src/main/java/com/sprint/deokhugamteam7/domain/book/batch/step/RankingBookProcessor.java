package com.sprint.deokhugamteam7.domain.book.batch.step;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.entity.RankingBook;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@StepScope
public class RankingBookProcessor implements ItemProcessor<RankingBook,RankingBook> {

  private final ReviewRepository reviewRepository;

  @Override
  public RankingBook process(RankingBook rankingBook) {
    rankingBook.reset();
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime after = calculateDateTime(now, rankingBook.getPeriod());

    List<Review> reviews = reviewRepository.findAllByBookAndCreatedAtBetweenAndIsDeletedIsFalse(
        rankingBook.getBook(), after, now);

    for (Review review : reviews) {
      rankingBook.update(review.getRating(), false);
    }

    return rankingBook;
  }

  private LocalDateTime calculateDateTime(LocalDateTime now, Period period) {
    return switch (period) {
      case DAILY -> now.minusDays(1);
      case WEEKLY -> now.minusWeeks(1);
      case MONTHLY -> now.minusMonths(1);
      default -> now.minusYears(1);
    };
  }
}
