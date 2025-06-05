package com.sprint.deokhugamteam7.domain.user.service;

import com.sprint.deokhugamteam7.constant.Period;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PowerUserScoreSchedule {
  private final PowerUserService powerUserService;

  @Scheduled(cron = "0 0/1 * * * *")
//  @Scheduled(cron = "0 0 09 * * *")
  public void calculatePowerUserScoresDaily() {
    LocalDate today = LocalDate.now();
//    log.info("파워 유저 점수 계산 시작: {}", today);

    for (Period period : Period.values()) {
//      log.info("기간: {}", period);
      powerUserService.calculateAndSaveUserScores(period, today);
      powerUserService.updateRanksForPeriodAndDate(period, today);
    }
//    log.info("파워 유저 점수 계산 완료");
  }
}
