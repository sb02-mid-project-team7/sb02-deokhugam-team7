package com.sprint.deokhugamteam7.domain.book.batch.tasklet;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.repository.RankingBookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component("deleteRankingBookTasklet")
@StepScope
public class DeleteRankingBookTasklet implements Tasklet {

  private final RankingBookRepository rankingBookRepository;

  private final Period period;

  public DeleteRankingBookTasklet(
      RankingBookRepository rankingBookRepository,
      @Value("#{jobParameters['period']}") String periodStr) {
    this.rankingBookRepository = rankingBookRepository;
    this.period = Period.valueOf(periodStr.toUpperCase());
  }

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    log.info("랭킹 북 삭제 시퀸스 작동, {}",period.toString());
    rankingBookRepository.deleteByPeriod(period);

    return RepeatStatus.FINISHED;
  }
}
