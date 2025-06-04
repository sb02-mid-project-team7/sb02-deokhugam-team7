package com.sprint.deokhugamteam7.domain.user.repository;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.user.dto.PowerUserSearchCondition;
import com.sprint.deokhugamteam7.domain.user.dto.UserActivity;
import com.sprint.deokhugamteam7.domain.user.entity.UserScore;
import java.time.LocalDate;
import java.util.List;

public interface UserQueryRepository {
  List<UserScore> findPowerUserScoresByPeriod(PowerUserSearchCondition condition);

  List<UserActivity> collectUserActivityScores(Period period, LocalDate baseDate);

  Long countByCondition(PowerUserSearchCondition condition);
}
