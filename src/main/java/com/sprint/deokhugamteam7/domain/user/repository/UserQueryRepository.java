package com.sprint.deokhugamteam7.domain.user.repository;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.user.dto.request.UserActivity;
import com.sprint.deokhugamteam7.domain.user.dto.response.CursorPageResponsePowerUserDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Sort;

public interface UserQueryRepository {
  CursorPageResponsePowerUserDto findPowerUsersByPeriod(
      Period period,
      Double cursor,
      LocalDateTime cursorCreatedAt,
      int size,
      Sort.Direction direction
  );

  List<UserActivity> collectUserActivityScores(Period period, LocalDate baseDate);
}
