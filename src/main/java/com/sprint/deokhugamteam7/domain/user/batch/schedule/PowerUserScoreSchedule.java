package com.sprint.deokhugamteam7.domain.user.batch.schedule;

import com.sprint.deokhugamteam7.constant.Period;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PowerUserScoreSchedule {

  private final JobLauncher jobLauncher;
  private final Job userScoreJob;

//  @Scheduled(cron = "0 2 0 * * *")
  public void runUserScoreBatchJob() {
    try {
      LocalDate today = LocalDate.now();

      for (Period period : Period.values()) {
        JobParameters params = new JobParametersBuilder()
            .addString("period", period.name())
            .addString("baseDate", today.toString())
            .addLong("timestamp", System.currentTimeMillis()) // 중복 실행 방지
            .toJobParameters();

        log.info("배치 잡 실행: period={}, baseDate={}", period, today);
        JobExecution execution = jobLauncher.run(userScoreJob, params);
        log.info("잡 상태: {}", execution.getStatus());
      }

    } catch (Exception e) {
      log.error("배치 실행 중 예외 발생", e);
    }
  }
}
