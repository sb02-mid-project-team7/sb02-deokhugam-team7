package com.sprint.deokhugamteam7.domain.user.repository;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.user.dto.UserActivity;
import com.sprint.deokhugamteam7.domain.user.entity.UserScore;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Sort;

public interface UserQueryRepository {
  List<UserScore> findPowerUserScoresByPeriod(
      Period period, Double cursorScore, LocalDateTime afterCreatedAt, int size, Sort.Direction direction);

  List<UserActivity> collectUserActivityScores(Period period, LocalDate baseDate);
}
