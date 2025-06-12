package com.sprint.deokhugamteam7.domain.user.batch.step;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.user.dto.UserActivity;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.entity.UserScore;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@StepScope
public class UserScoreProcessor implements ItemProcessor<UserActivity, UserScore> {

  private final UserRepository userRepository;

  @Value("#{jobParameters['period']}")
  private String periodStr;

  @Override
  public UserScore process(UserActivity activity) {
    UUID userId = activity.userId();
    User user = userRepository.findById(userId).orElseThrow();

    Period period = Period.valueOf(periodStr.toUpperCase());

    return UserScore.create(
        user,
        period,
        activity.reviewScoreSum(),
        activity.likeCount(),
        activity.commentCount()
    );
  }
}