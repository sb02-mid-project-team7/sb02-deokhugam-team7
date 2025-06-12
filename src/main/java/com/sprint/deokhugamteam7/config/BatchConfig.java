package com.sprint.deokhugamteam7.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

  @Bean
  public Job userScoreJob(
    JobRepository jobRepository,
    @Qualifier("collectAndSaveUserScoresStep") Step collectAndSaveUserScoresStep,
    @Qualifier("updateUserRankingStep") Step updateUserRankingStep
  ) {
    return new JobBuilder("userScoreJob", jobRepository)
      .start(collectAndSaveUserScoresStep)
      .next(updateUserRankingStep)
      .build();
  }

  @Bean
  public Step collectAndSaveUserScoresStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      Tasklet userScoreTasklet
  ) {
    return new StepBuilder("collectAndSaveUserScoresStep", jobRepository)
        .tasklet(userScoreTasklet, transactionManager)
        .build();
  }

  @Bean
  public Step updateUserRankingStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      Tasklet rankUpdateTasklet
  ) {
    return new StepBuilder("updateUserRankingStep", jobRepository)
        .tasklet(rankUpdateTasklet, transactionManager)
        .build();
  }
}