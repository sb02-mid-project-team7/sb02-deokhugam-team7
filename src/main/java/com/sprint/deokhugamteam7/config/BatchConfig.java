package com.sprint.deokhugamteam7.config;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

  @Bean
  public Job userScoreJob(
      JobRepository jobRepository,
      Step collectAndSaveUserScoresStep,
      Step updateUserRankingStep
  ) {
    return new JobBuilder("userScoreJob", jobRepository)
        .start(collectAndSaveUserScoresStep)
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
      Tasklet rankUpdateTasklet // 실제 랭킹 갱신 로직이 구현
  ) {
    return new StepBuilder("updateUserRankingStep", jobRepository)
        .tasklet(rankUpdateTasklet, transactionManager)
        .build();
  }
}