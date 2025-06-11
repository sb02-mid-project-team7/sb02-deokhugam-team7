package com.sprint.deokhugamteam7.domain.book.batch.step;

import com.sprint.deokhugamteam7.domain.book.dto.FindPopularBookDto;
import com.sprint.deokhugamteam7.domain.book.entity.RankingBook;
import com.sprint.deokhugamteam7.domain.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@StepScope
public class RankingBookProcessor implements ItemProcessor<FindPopularBookDto,RankingBook> {

  private final BookRepository bookRepository;

  @Value("#{jobParameters['period']}")
  private String periodStr;

  @Value("#{jobParameters['baseDate']}")
  private String baseDateStr;

  @Override
  public RankingBook process(FindPopularBookDto item) throws Exception {
    return null;
  }
}
