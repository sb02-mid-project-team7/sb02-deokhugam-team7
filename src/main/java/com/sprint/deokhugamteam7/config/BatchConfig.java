package com.sprint.deokhugamteam7.config;

import com.sprint.deokhugamteam7.domain.book.dto.BookActivity;
import com.sprint.deokhugamteam7.domain.book.entity.RankingBook;
import com.sprint.deokhugamteam7.domain.review.dto.ReviewActivity;
import com.sprint.deokhugamteam7.domain.review.entity.RankingReview;
import com.sprint.deokhugamteam7.domain.user.dto.UserActivity;
import com.sprint.deokhugamteam7.domain.user.entity.UserScore;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
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
      @Qualifier("deleteUserScoreStep") Step deleteUserScoreStep,
      @Qualifier("collectAndSaveUserScoresStep") Step collectAndSaveUserScoresStep,
      @Qualifier("updateUserRankingStep") Step updateUserRankingStep
  ) {
    return new JobBuilder("userScoreJob", jobRepository)
        .start(deleteUserScoreStep)
        .next(collectAndSaveUserScoresStep)
        .next(updateUserRankingStep)
        .build();
  }

  @Bean
  public Step collectAndSaveUserScoresStep(
      JobRepository jobRepository, // Job과 Step의 메타데이터 저장 및 관리
      PlatformTransactionManager transactionManager, // Chunk 단위 트랜잭션을 관리
      ItemReader<UserActivity> reader, // 사용자 활동 데이터를 조회
      ItemProcessor<UserActivity, UserScore> processor, // 읽은 데이터를 UserScore 엔티티로 변환
      ItemWriter<UserScore> writer // UserScore 데이터를 DB에 저장
  ) {
    return new StepBuilder("collectAndSaveUserScoresStep", jobRepository)
        .<UserActivity, UserScore>chunk(100, transactionManager)
        .reader(reader)
        .processor(processor)
        .writer(writer)
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
  public Step deleteUserScoreStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      Tasklet deleteUserScoreTasklet
  ) {
    return new StepBuilder("deleteUserScoreStep", jobRepository)
        .tasklet(deleteUserScoreTasklet, transactionManager)
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
      @Qualifier("deleteRankingBookStep") Step deleteRankingBookStep,
      @Qualifier("updateRankingBooksStep") Step updateRankingBooksStep,
      @Qualifier("updateBookRankingStep") Step updateBookRankingStep
  ) {
    return new JobBuilder("rankingBookJob", jobRepository)
        .start(deleteRankingBookStep)
        .next(updateRankingBooksStep)
        .next(updateBookRankingStep)
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
      ItemReader<BookActivity> reader,
      ItemProcessor<BookActivity, RankingBook> processor,
      ItemWriter<RankingBook> writer
  ) {
    return new StepBuilder("updateRankingBooksStep", jobRepository)
        .<BookActivity, RankingBook>chunk(10, transactionManager)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .build();
  }

  @Bean
  public Step deleteRankingBookStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      Tasklet DeleteRankingBookTasklet
  ) {
    return new StepBuilder("deleteRankingBookStep", jobRepository)
        .tasklet(DeleteRankingBookTasklet, transactionManager)
        .build();
  }

  @Bean
  public Step updateBookRankingStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      Tasklet BookRankUpdateTasklet
  ) {
    return new StepBuilder("updateBookRankingStep", jobRepository)
        .tasklet(BookRankUpdateTasklet, transactionManager)
        .build();
  }
}
