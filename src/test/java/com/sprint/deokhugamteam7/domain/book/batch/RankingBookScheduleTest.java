package com.sprint.deokhugamteam7.domain.book.batch;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.batch.schedule.RankingBookSchedule;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

@ExtendWith(MockitoExtension.class)
class RankingBookScheduleTest {

  @Mock
  private JobLauncher jobLauncher;

  @Mock
  private Job rankingBookJob;

  @Mock
  private JobExecution jobExecution;

  private RankingBookSchedule schedule;

  @BeforeEach
  void setUp() {
    schedule = new RankingBookSchedule(jobLauncher, rankingBookJob);
  }

  @Test
  @DisplayName("calculateBookScore: 정상적으로 JobLauncher.run을 호출하고 Status를 얻는다")
  void calculateBookScore_successfulLaunch() throws Exception {
    // given
    when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);
    when(jobLauncher.run(eq(rankingBookJob), any(JobParameters.class)))
        .thenReturn(jobExecution);

    LocalDateTime start = LocalDateTime.of(2025, 6, 10, 0, 0);
    LocalDateTime end   = LocalDateTime.of(2025, 6, 11, 0, 0);
    Period period = Period.DAILY;

    // when
    schedule.calculateBookScore(start, end, period);

    // then
    ArgumentCaptor<JobParameters> paramsCaptor = ArgumentCaptor.forClass(JobParameters.class);
    verify(jobLauncher).run(eq(rankingBookJob), paramsCaptor.capture());

    JobParameters params = paramsCaptor.getValue();
    assertThat(params.getString("start")).isEqualTo(start.toString());
    assertThat(params.getString("end")).isEqualTo(end.toString());
    assertThat(params.getString("period")).isEqualTo(period.name());
    assertThat(params.getLong("timestamp")).isNotNull();
  }

  @Test
  @DisplayName("calculateBookScore: JobLauncher.run에서 예외가 발생해도 예외를 던지지 않는다")
  void calculateBookScore_launchThrows_shouldNotPropagate() throws Exception {
    // given
    doThrow(new RuntimeException("fail"))
        .when(jobLauncher).run(eq(rankingBookJob), any(JobParameters.class));

    // when / then: 예외가 내부에서 캐치되어 외부로는 던지지 않음
    assertDoesNotThrow(() -> schedule.calculateBookScore(null, null, Period.ALL_TIME));

    // and: run은 호출됐어야 함
    verify(jobLauncher).run(eq(rankingBookJob), any(JobParameters.class));
  }
}
