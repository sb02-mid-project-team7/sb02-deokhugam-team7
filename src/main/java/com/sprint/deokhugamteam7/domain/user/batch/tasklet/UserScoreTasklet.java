package com.sprint.deokhugamteam7.domain.user.batch.tasklet;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.user.service.PowerUserService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
public class UserScoreTasklet implements Tasklet {
  private final PowerUserService powerUserService;

  @Value("#{jobParameters['period']}")
  private String periodStr;

  @Value("#{jobParameters['baseDate']}")
  private String baseDateStr;

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    Period period = Period.valueOf(periodStr);
    LocalDate date = LocalDate.parse(baseDateStr);

    powerUserService.calculateAndSaveUserScores(period, date);

    return RepeatStatus.FINISHED;
  }
}
