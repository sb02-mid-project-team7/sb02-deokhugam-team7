package com.sprint.deokhugamteam7.domain.book.batch.step;

import com.sprint.deokhugamteam7.domain.book.entity.RankingBook;
import com.sprint.deokhugamteam7.domain.book.repository.RankingBookRepository;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@StepScope
public class RankingBookReader implements ItemReader<RankingBook> {

  private final RankingBookRepository rankingBookRepository;
  private Iterator<RankingBook> rankingBookIterator;

  @Override
  public RankingBook read(){
    if (rankingBookIterator == null) {
      List<RankingBook> all = rankingBookRepository.findAll();
      rankingBookIterator = all.iterator();
    }
    return rankingBookIterator.hasNext() ? rankingBookIterator.next() : null;
  }
}
