package com.sprint.deokhugamteam7.domain.user.service;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.user.dto.response.CursorPageResponsePowerUserDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.domain.Sort;

public interface PowerUserService {

  CursorPageResponsePowerUserDto getPowerUsers(
      Period period, String cursor, LocalDateTime after, int size, Sort.Direction direction
  );

  void calculateAndSaveUserScores(Period period, LocalDate baseDate);

  void updateRanksForPeriodAndDate(Period period, LocalDate date);

}
