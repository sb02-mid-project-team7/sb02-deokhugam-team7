package com.sprint.deokhugamteam7.domain.book.batch.schedule;

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
public class RankingBookSchedule {

  private final JobLauncher jobLauncher;
  private final Job rankingBookJob;

  @Scheduled(cron = "0 0/1 * * * *")
  public void runRankingJob() {
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
      log.info("인기 도서 - 배치 실행 시도: period={}, start={}, end={}", period, start, end);
      JobExecution execution = jobLauncher.run(rankingBookJob, jobParameters);
      log.info("인기 도서 - 배치 상태: {}", execution.getStatus());
    } catch (Exception e) {
      log.error("인기 도서- 배치 실패 - period: {}, start: {}, end: {}", period, start, end, e);
    }
  }



}
