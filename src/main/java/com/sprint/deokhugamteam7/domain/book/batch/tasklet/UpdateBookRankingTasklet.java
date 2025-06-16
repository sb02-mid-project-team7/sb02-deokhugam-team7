package com.sprint.deokhugamteam7.domain.book.batch.tasklet;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.service.BookSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component("updateBookRankingTasklet")
@StepScope
public class UpdateBookRankingTasklet implements Tasklet {

  private final BookSearchService bookSearchService;

  private Period period;

  public UpdateBookRankingTasklet(BookSearchService bookSearchService, @Value("#{jobParameters['period']}") String periodStr) {
    this.bookSearchService = bookSearchService;
    this.period = Period.valueOf(periodStr.toUpperCase());
  }

  public RepeatStatus execute(StepContribution contributionm, ChunkContext chunkContext) {
    log.info("랭킹 북 랭크 업데이트 시퀸스 작동, {}", period.toString());
    bookSearchService.updateRanksForPeriod(period);

    return RepeatStatus.FINISHED;
  }
}
