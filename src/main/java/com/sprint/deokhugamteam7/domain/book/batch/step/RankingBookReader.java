package com.sprint.deokhugamteam7.domain.book.batch.step;

import com.sprint.deokhugamteam7.domain.book.dto.BookActivity;
import com.sprint.deokhugamteam7.domain.book.repository.RankingBookRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class RankingBookReader implements ItemReader<BookActivity> {

  private final RankingBookRepository rankingBookRepository;
  private List<BookActivity> data;
  private int index = 0;
  private final LocalDateTime start;
  private final LocalDateTime end;

  public RankingBookReader(
      RankingBookRepository rankingBookRepository,
      @Value("#{jobParameters['start']}") String startStr,
      @Value("#{jobParameters['end']}") String endStr) {
    this.rankingBookRepository = rankingBookRepository;
    this.start = !startStr.isBlank() ? LocalDateTime.parse(startStr) : null;
    this.end = !endStr.isBlank() ? LocalDateTime.parse(endStr) : null;
  }

  @Override
  public BookActivity read() {
    if (data == null) {
      data = rankingBookRepository.findReviewActivitySummary(start, end);
    }
    return index < data.size() ? data.get(index++) : null;
  }
}
