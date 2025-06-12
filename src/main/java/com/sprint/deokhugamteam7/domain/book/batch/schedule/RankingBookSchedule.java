package com.sprint.deokhugamteam7.domain.book.batch.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingBookSchedule {

  private final JobLauncher jobLauncher;
  private final Job rankingBookJob;

  @Scheduled(cron = "0 0/1 * * * *")
  public void runRankingJob() {
    try {
      JobParameters jobParameters = new JobParametersBuilder()
          .addLong("time", System.currentTimeMillis()) // 중복 실행 방지
          .toJobParameters();
      log.info("[RankingBookSchedule] run batch job");
      JobExecution execution = jobLauncher.run(rankingBookJob, jobParameters);
      log.info("[RankingBookSchedule] batch job status: {}", execution.getStatus());
    } catch (Exception e) {
      log.error("[RankingBookSchedule] fail batch job", e);
    }
  }
}
