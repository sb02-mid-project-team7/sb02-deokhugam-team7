package com.sprint.deokhugamteam7.domain.user.service;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.user.dto.PowerUserSearchCondition;
import com.sprint.deokhugamteam7.domain.user.dto.response.CursorPageResponsePowerUserDto;
import java.time.LocalDate;

public interface PowerUserService {

  CursorPageResponsePowerUserDto getPowerUsers(PowerUserSearchCondition condition);

  void calculateAndSaveUserScores(Period period, LocalDate baseDate);

  void updateRanksForPeriodAndDate(Period period, LocalDate date);

}
