package com.sprint.deokhugamteam7.domain.user.service.impl;

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
import com.sprint.deokhugamteam7.domain.user.service.PowerUserService;
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
    log.info("파워 유저 목록 조회 요청: period={}, direction={}, cursor={}, after={}, limit={}",
        condition.period(), condition.direction(), condition.cursor(), condition.after(), condition.size());
    List<UserScore> results = userQueryRepository.findPowerUserScoresByPeriod(condition);

    List<PowerUserDto> content = results.stream()
        .map(PowerUserDto::from)
        .toList();

    boolean hasNext = results.size() > condition.size();
    if (hasNext) {
      results.remove(condition.size());
    }

    String nextCursor = hasNext ? String.valueOf(results.get(results.size() - 1).getScore()) : null;
    String nextAfter = hasNext ? results.get(results.size() - 1).getCreatedAt().toString() : null;

    log.info("파워 유저 목록 조회 완료: size={}, hasNext={}, nextCursor={}, nextAfter={}",
        content.size(), hasNext, nextCursor, nextAfter);

    return new CursorPageResponsePowerUserDto(
        content,
        nextCursor,
        nextAfter,
        content.size(),
        content.size(), // 필요시 총 개수 쿼리 추가(프로토타입 화면에서는 총 개수 안보임)
        hasNext
    );
  }


  @Override
  public void calculateAndSaveUserScores(Period period, LocalDate baseDate) {
    log.info("파워 유저 점수 계산 시작: period={}, baseDate={}", period, baseDate);

    List<UserActivity> activities = userQueryRepository.collectUserActivityScores(period, baseDate);

    if (activities.isEmpty()) {
      log.info("점수 계산 대상 없음 - 저장 생략: period={}, baseDate={}", period, baseDate);
      return;
    }

    // 활동 대상 사용자들의 ID 추출
    List<UUID> userIds = activities.stream()
        .map(UserActivity::userId)
        .toList();

    // 사용자들을 Map으로 구성
    Map<UUID, User> userMap = userRepository.findAllById(userIds).stream()
        .collect(Collectors.toMap(User::getId, Function.identity()));

    List<UserScore> savedScores = userScoreRepository.findAllByPeriodAndDate(period, baseDate);

    // 기존 점수들을 Map으로 구성
    Map<UUID, UserScore> userScoreMap = savedScores.stream()
        .collect(Collectors.toMap(score -> score.getUser().getId(), Function.identity()));

    // 결과 집계 변수
    int inserted = 0;
    int updated = 0;
    int skipped = 0;

    // 점수 계산 및 저장
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
          updated++;
        } else {
          skipped++;
        }
      } else {
        UserScore newScore = UserScore.create(user, period, baseDate, reviewScoreSum, likeCount, commentCount);
        userScoreRepository.save(newScore);
        inserted++;
      }
    }

    log.info("유저 점수 저장 완료 - 신규: {}, 업데이트: {}, 생략: {}", inserted, updated, skipped);
  }

  @Override
  public void updateRanksForPeriodAndDate(Period period, LocalDate date) {
    log.info("파워 유저 랭킹 갱신 시작: period={}, date={}", period, date);

    List<UserScore> scores = userScoreRepository.findAllByPeriodAndDateOrderByScoreDesc(period, date);

    long rank = 1;
    for (UserScore score : scores) {
      score.updateRank(rank++);
    }

    userScoreRepository.saveAll(scores);
    log.info("파워 유저 랭킹 갱신 완료: 갱신된 유저 수={}", scores.size());
  }

}
