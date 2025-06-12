package com.sprint.deokhugamteam7.domain.review.batch.step;

import com.sprint.deokhugamteam7.domain.review.dto.ReviewActivity;
import com.sprint.deokhugamteam7.domain.review.repository.custom.ReviewRepositoryCustom;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class ReviewActivityReader implements ItemReader<ReviewActivity> {

  private final ReviewRepositoryCustom reviewRepositoryCustom;
  private List<ReviewActivity> data;
  private int index = 0;
  private final LocalDateTime start;
  private final LocalDateTime end;

  public ReviewActivityReader(
      ReviewRepositoryCustom reviewRepositoryCustom,
      @Value("#{jobParameters['start']}") String startStr,
      @Value("#{jobParameters['end']}") String endStr) {
    this.reviewRepositoryCustom = reviewRepositoryCustom;
    this.start = !startStr.isBlank() ? LocalDateTime.parse(startStr) : null;
    this.end = !endStr.isBlank() ? LocalDateTime.parse(endStr) : null;
  }

  @Override
  public ReviewActivity read() {
    if (data == null) {
      data = reviewRepositoryCustom.findReviewActivitySummary(start, end);
    }
    return index < data.size() ? data.get(index++) : null;
  }
}
