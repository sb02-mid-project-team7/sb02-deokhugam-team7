package com.sprint.deokhugamteam7.domain.review.batch.schedule;

import com.sprint.deokhugamteam7.constant.Period;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class PopularReviewScoreSchedule {

  private final JobLauncher jobLauncher;
  private final Job reviewRankingJob;

  @Scheduled(cron = "0 0 0 * * *")
  public void scheduleScore() {
    LocalDateTime end = LocalDate.now().atStartOfDay();

    calculateReviewScore(end.minusDays(1), end, Period.DAILY);
    calculateReviewScore(end.minusWeeks(1), end, Period.WEEKLY);
    calculateReviewScore(end.minusMonths(1), end, Period.MONTHLY);
    calculateReviewScore(null, null, Period.ALL_TIME);
  }

  public void calculateReviewScore
      (@Nullable LocalDateTime start, @Nullable LocalDateTime end, Period period) {
    JobParameters jobParameters = new JobParametersBuilder()
        .addString("start", start != null ? start.toString() : "")
        .addString("end", end != null ? end.toString() : "")
        .addString("period", period.name())
        .addLong("timestamp", System.currentTimeMillis())
        .toJobParameters();

    try {
      log.info("Ranking Review-배치 실행 시도: period={}, start={}, end={}", period, start, end);
      JobExecution jobExecution = jobLauncher.run(reviewRankingJob, jobParameters);
      log.info("Ranking Review-Job 실행 상태: {}", jobExecution.getStatus());
    } catch (Exception e) {
      log.error("리뷰 점수 배치 실패 - period: {}, start: {}, end: {}", period, start, end, e);
    }
  }
}
