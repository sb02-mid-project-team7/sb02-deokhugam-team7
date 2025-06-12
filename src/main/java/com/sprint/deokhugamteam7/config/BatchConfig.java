package com.sprint.deokhugamteam7.config;

import com.sprint.deokhugamteam7.domain.review.dto.ReviewActivity;
import com.sprint.deokhugamteam7.domain.review.entity.RankingReview;
import com.sprint.deokhugamteam7.domain.book.entity.RankingBook;
import com.sprint.deokhugamteam7.domain.user.dto.UserActivity;
import com.sprint.deokhugamteam7.domain.user.entity.UserScore;
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

  @Bean
  public Job reviewRankingJob(
      JobRepository jobRepository,
      @Qualifier("updateRankingReviewStep") Step updateRankingReviewStep
  ) {
    return new JobBuilder("reviewRankingJob", jobRepository)
        .start(updateRankingReviewStep)        
        .build();
  }
  
  @Bean
  public Job rankingBookJob(
      JobRepository jobRepository,
      @Qualifier("updateRankingBooksStep") Step updateRankingBooksStep
  ) {
    return new JobBuilder("rankingBookJob", jobRepository)
        .start(updateRankingBooksStep)
        .build();
  }

  @Bean
  public Step updateRankingReviewStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      ItemReader<ReviewActivity> reader,
      ItemProcessor<ReviewActivity, RankingReview> processor,
      ItemWriter<RankingReview> writer
  ) {
    return new StepBuilder("updateRankingReviewStep", jobRepository)
        .<ReviewActivity, RankingReview>chunk(100, transactionManager)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .build();
  }
  
  @Bean
  public Step updateRankingBooksStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      ItemReader<RankingBook> reader,
      ItemProcessor<RankingBook, RankingBook> processor,
      ItemWriter<RankingBook> writer
  ) {
    return new StepBuilder("updateRankingBooksStep", jobRepository)
        .<RankingBook, RankingBook>chunk(10, transactionManager)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .build();
  }
}