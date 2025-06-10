package com.sprint.deokhugamteam7.domain.user.batch.step;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.user.dto.UserActivity;
import com.sprint.deokhugamteam7.domain.user.repository.UserQueryRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class UserActivityReader implements ItemReader<UserActivity> {

  private final UserQueryRepository userQueryRepository;
  private List<UserActivity> data;
  private int index = 0;
  private final Period period;
  private final LocalDate baseDate;

  public UserActivityReader(
      UserQueryRepository userQueryRepository,
      @Value("#{jobParameters['period']}") String periodStr,
      @Value("#{jobParameters['baseDate']}") String baseDateStr)
  {
    this.userQueryRepository = userQueryRepository;
    this.period = Period.valueOf(periodStr.toUpperCase());
    this.baseDate = LocalDate.parse(baseDateStr);
  }

  @Override
  public UserActivity read() {
    if (data == null) {
      data = userQueryRepository.collectUserActivityScores(period, baseDate);
    }
    return index < data.size() ? data.get(index++) : null;
  }
}