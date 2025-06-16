package com.sprint.deokhugamteam7.domain.book.batch.step;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.entity.RankingBook;
import com.sprint.deokhugamteam7.domain.book.repository.RankingBookRepository;
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
    List<? extends RankingBook> list = chunk.getItems();

    Set<UUID> bookIds = list.stream()
        .map(b -> b.getBook().getId()).collect(Collectors.toSet());
    Map<UUID, RankingBook> existingMap =
        rankingBookRepository.findAllByBookIdInAndPeriod(bookIds, period);

    List<RankingBook> toSave = new ArrayList<>();

    for (RankingBook rankingBook : list) {
      RankingBook existing = existingMap.get(rankingBook.getBook().getId());
      if (existing != null) {
        existing.updateScore(rankingBook.getScore());
      }
      toSave.add(rankingBook);
    }

    rankingBookRepository.saveAll(toSave);
  }
}
