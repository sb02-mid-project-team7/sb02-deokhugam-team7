package com.sprint.deokhugamteam7.domain.user.service.impl;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.user.dto.request.UserActivity;
import com.sprint.deokhugamteam7.domain.user.dto.response.CursorPageResponsePowerUserDto;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.entity.UserScore;
import com.sprint.deokhugamteam7.domain.user.repository.UserQueryRepository;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import com.sprint.deokhugamteam7.domain.user.repository.UserScoreRepository;
import com.sprint.deokhugamteam7.domain.user.service.PowerUserService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class PowerUserServiceImpl implements PowerUserService {

  private final UserQueryRepository userQueryRepository;
  private final UserRepository userRepository;
  private final UserScoreRepository userScoreRepository;

  @Override
  @Transactional(readOnly = true)
  public CursorPageResponsePowerUserDto getPowerUsers(
      Period period, String cursor, LocalDateTime after, int size, Sort.Direction direction
  ) {
    Double cursorScore = cursor != null ? Double.parseDouble(cursor) : null;
    return userQueryRepository.findPowerUsersByPeriod(period, cursorScore, after, size, direction);
  }

  @Override
  public void calculateAndSaveUserScores(Period period, LocalDate baseDate) {
    userScoreRepository.deleteByPeriodAndDate(period, baseDate);

    List<UserActivity> activities = userQueryRepository.collectUserActivityScores(period, baseDate);
    if (activities.isEmpty()) return;

    Map<UUID, User> userMap = userRepository.findAllById(
        activities.stream().map(UserActivity::userId).toList()
    ).stream().collect(Collectors.toMap(User::getId, Function.identity()));

    List<UserScore> scores = activities.stream()
        .map(activity -> UserScore.create(
            userMap.get(activity.userId()),
            period,
            baseDate,
            activity.reviewScoreSum(),
            activity.likeCount(),
            activity.commentCount()
        )).toList();

    userScoreRepository.saveAll(scores);
  }

  @Override
  public void updateRanksForPeriodAndDate(Period period, LocalDate date) {
    List<UserScore> scores = userScoreRepository.findAllByPeriodAndDateOrderByScoreDesc(period, date);

    long rank = 1;
    for (UserScore score : scores) {
      score.updateRank(rank++);
    }

    userScoreRepository.saveAll(scores);
  }

}
