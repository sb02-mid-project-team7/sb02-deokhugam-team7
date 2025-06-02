package com.sprint.deokhugamteam7.domain.user.service.impl;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.user.dto.UserActivity;
import com.sprint.deokhugamteam7.domain.user.dto.response.CursorPageResponsePowerUserDto;
import com.sprint.deokhugamteam7.domain.user.dto.response.PowerUserDto;
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

    List<UserScore> results = userQueryRepository.findPowerUserScoresByPeriod(
        period, cursorScore, after, size, direction
    );

    boolean hasNext = results.size() > size;
    if (hasNext) {
      results.remove(size); // 마지막 요소 제거
    }

    List<PowerUserDto> content = results.stream()
        .map(us -> new PowerUserDto(
            us.getUser().getId(),
            us.getUser().getNickname(),
            us.getPeriod(),
            us.getCreatedAt(),
            us.getRank() != null ? us.getRank() : 0L,
            us.getScore(),
            us.getReviewScoreSum(),
            us.getLikeCount(),
            us.getCommentCount()
        )).toList();

    String nextCursor = hasNext ? String.valueOf(results.get(results.size() - 1).getScore()) : null;
    String nextAfter = hasNext ? results.get(results.size() - 1).getCreatedAt().toString() : null;

    return new CursorPageResponsePowerUserDto(
        content,
        nextCursor,
        nextAfter,
        content.size(),
        content.size(), // 필요시 총 개수 쿼리 추가
        hasNext
    );
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
