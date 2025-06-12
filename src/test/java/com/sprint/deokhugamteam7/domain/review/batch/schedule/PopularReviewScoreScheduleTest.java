package com.sprint.deokhugamteam7.domain.review.batch.schedule;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.constant.Period;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

@ExtendWith(MockitoExtension.class)
class PopularReviewScoreScheduleTest {

  @Mock
  private JobLauncher jobLauncher;

  @Mock
  private Job reviewRankingJob;

  @InjectMocks
  private PopularReviewScoreSchedule schedule;

  @Test
  @DisplayName("scheduleScore()는 calculateReviewScore를 4번 호출")
  void scheduleScore_ShouldRunJobFourTimes() throws Exception {
    given(jobLauncher.run(any(), any())).willReturn(mock(JobExecution.class));

    schedule.scheduleScore();

    verify(jobLauncher, times(4)).run(eq(reviewRankingJob), any(JobParameters.class));
  }

  @Test
  @DisplayName("배치 실행 중 예외 발생 시 로그 에러 호출")
  void calculateReviewScore_WhenExceptionThrown_ShouldLogError() throws Exception {
    LocalDateTime start = LocalDateTime.now().minusDays(1);
    LocalDateTime end = LocalDateTime.now();
    Period period = Period.DAILY;

    when(jobLauncher.run(any(), any())).thenThrow(new RuntimeException("배치 실행 실패"));

    schedule.calculateReviewScore(start, end, period);

    verify(jobLauncher).run(any(), any());
  }
}