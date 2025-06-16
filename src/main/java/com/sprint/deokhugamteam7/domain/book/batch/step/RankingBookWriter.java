package com.sprint.deokhugamteam7.domain.book.batch.step;

import com.sprint.deokhugamteam7.domain.book.entity.RankingBook;
import com.sprint.deokhugamteam7.domain.book.repository.RankingBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@StepScope
public class RankingBookWriter implements ItemWriter<RankingBook> {
  private final RankingBookRepository rankingBookRepository;

  @Override
  public void write(Chunk<? extends RankingBook> chunk) throws Exception {
    rankingBookRepository.saveAll(chunk);
  }
}
