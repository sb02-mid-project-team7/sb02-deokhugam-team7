package com.sprint.deokhugamteam7.domain.book.batch.step;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.entity.RankingBook;
import com.sprint.deokhugamteam7.domain.book.repository.RankingBookRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class RankingBookWriter implements ItemWriter<RankingBook> {
  private final RankingBookRepository rankingBookRepository;
  private final Period period;

  public RankingBookWriter(
      RankingBookRepository rankingBookRepository,
      @Value("#{jobParameters['period']}") String periodStr
  ) {
    this.rankingBookRepository = rankingBookRepository;
    this.period = Period.valueOf(periodStr.toUpperCase());
  }

  @Override
  public void write(Chunk<? extends RankingBook> chunk) throws Exception {
    List<? extends RankingBook> inputList = chunk.getItems();

    Set<UUID> bookIds = inputList.stream()
        .map(rb -> rb.getBook().getId())
        .collect(Collectors.toSet());

    List<RankingBook> existing = rankingBookRepository.findAllByBookIdInAndPeriod(bookIds, period);
    Map<UUID, RankingBook> existingMap = existing.stream()
        .collect(Collectors.toMap(rb -> rb.getBook().getId(), Function.identity()));

    List<RankingBook> toSave = new ArrayList<>();

    for (RankingBook newEntry : inputList) {
      UUID bookId = newEntry.getBook().getId();
      RankingBook existingEntry = existingMap.get(bookId);

      if (existingEntry != null) {
        existingEntry.updateScore(newEntry.getScore());
        existingEntry.updateRating(newEntry.getRating());
        existingEntry.updateReviewCount(newEntry.getReviewCount());
        toSave.add(existingEntry);
      } else {
        toSave.add(newEntry);
      }
    }
    rankingBookRepository.saveAll(toSave);
  }
}
