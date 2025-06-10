package com.sprint.deokhugamteam7.domain.user.service;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.user.dto.PowerUserSearchCondition;
import com.sprint.deokhugamteam7.domain.user.dto.UserActivity;
import com.sprint.deokhugamteam7.domain.user.dto.response.CursorPageResponsePowerUserDto;
import com.sprint.deokhugamteam7.domain.user.dto.response.PowerUserDto;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.entity.UserScore;
import com.sprint.deokhugamteam7.domain.user.repository.UserQueryRepository;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import com.sprint.deokhugamteam7.domain.user.repository.UserScoreRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class PowerUserServiceImpl implements PowerUserService {

  private final UserQueryRepository userQueryRepository;
  private final UserRepository userRepository;
  private final UserScoreRepository userScoreRepository;

  @Override
  @Transactional(readOnly = true)
  public CursorPageResponsePowerUserDto getPowerUsers(PowerUserSearchCondition condition) {
    List<UserScore> results = userQueryRepository.findPowerUserScoresByPeriod(condition);

    List<UserScore> trimmedResults = results.size() > condition.getSize()
        ? results.subList(0, condition.getSize())
        : results;

    List<PowerUserDto> content = trimmedResults.stream()
        .map(PowerUserDto::from)
        .toList();

    boolean hasNext = results.size() > condition.getSize();
    if (hasNext) {
      results.remove(condition.getSize());
    }

    String nextCursor = hasNext ? String.valueOf(results.get(results.size() - 1).getScore()) : null;
    String nextAfter = hasNext ? results.get(results.size() - 1).getCreatedAt().toString() : null;

    long total = userQueryRepository.countByCondition(condition);

    return new CursorPageResponsePowerUserDto(
        content,
        nextCursor,
        nextAfter,
        content.size(),
        total,
        hasNext
    );
  }

  @Override
  public void calculateAndSaveUserScores(Period period, LocalDate baseDate) {
    List<UserActivity> activities = userQueryRepository.collectUserActivityScores(period, baseDate);

    if (activities.isEmpty()) {
      return;
    }

    List<UUID> userIds = activities.stream()
        .map(UserActivity::userId)
        .toList();

    Map<UUID, User> userMap = userRepository.findAllById(userIds).stream()
        .collect(Collectors.toMap(User::getId, Function.identity()));

    List<UserScore> savedScores = userScoreRepository.findAllByPeriodAndDate(period, baseDate);
    Map<UUID, UserScore> userScoreMap = savedScores.stream()
        .collect(Collectors.toMap(score -> score.getUser().getId(), Function.identity()));

    for (UserActivity activity : activities) {
      UUID userId = activity.userId();
      User user = userMap.get(userId);
      UserScore existing = userScoreMap.get(userId);

      double reviewScoreSum = activity.reviewScoreSum();
      long likeCount = activity.likeCount();
      long commentCount = activity.commentCount();

      if (existing != null) {
        if (!existing.isSameScores(reviewScoreSum, likeCount, commentCount)) {
          existing.updateScores(reviewScoreSum, likeCount, commentCount);
          userScoreRepository.save(existing);
        }
      } else {
        UserScore newScore = UserScore.create(user, period, baseDate, reviewScoreSum, likeCount, commentCount);
        userScoreRepository.save(newScore);
      }
    }
  }


  @Override
  public void updateRanksForPeriodAndDate(Period period, LocalDate date) {
    List<UserScore> scores = userScoreRepository.findAllByPeriodAndDateOrderByScoreDesc(period, date);
    if (scores.isEmpty()) return;

    long rank = 1;
    for (UserScore score : scores) {
      score.updateRank(rank++);
    }
    userScoreRepository.saveAll(scores);
  }

}
