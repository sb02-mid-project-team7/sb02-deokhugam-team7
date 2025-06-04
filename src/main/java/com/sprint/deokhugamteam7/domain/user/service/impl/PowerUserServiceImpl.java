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
        condition.getPeriod(), condition.getDirection(), condition.getCursor(), condition.getAfter(),
        condition.getSize());
    List<UserScore> results;
    try {
      results = userQueryRepository.findPowerUserScoresByPeriod(condition);
    } catch (Exception e) {
      log.error("파워 유저 조회 실패 - condition={}, error={}", condition, e.getMessage(), e);
      throw new IllegalStateException("파워 유저 조회 중 오류 발생", e);
    }

    List<PowerUserDto> content;
    try {
      List<UserScore> trimmedResults = results.size() > condition.getSize()
          ? results.subList(0, condition.getSize())
          : results;
      content = trimmedResults.stream()
          .map(PowerUserDto::from)
          .toList();
    } catch (Exception e) {
      log.error("PowerUserDto 매핑 실패 - error={}", e.getMessage(), e);
      throw new IllegalStateException("결과 매핑 중 오류 발생", e);
    }

    boolean hasNext = results.size() > condition.getSize();
    if (hasNext) {
      results.remove(condition.getSize());
    }

    String nextCursor = hasNext ? String.valueOf(results.get(results.size() - 1).getScore()) : null;
    String nextAfter = hasNext ? results.get(results.size() - 1).getCreatedAt().toString() : null;

    long total;
    try {
      total = userQueryRepository.countByCondition(condition);
    } catch (Exception e) {
      log.error("총 파워 유저 수 조회 실패 - error={}", e.getMessage(), e);
      throw new IllegalStateException("파워 유저 개수 조회 중 오류 발생", e);
    }

    log.info("파워 유저 목록 조회 완료: size={}, hasNext={}, nextCursor={}, nextAfter={}",
        content.size(), hasNext, nextCursor, nextAfter);

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
    log.info("파워 유저 점수 계산 시작: period={}, baseDate={}", period, baseDate);

    List<UserActivity> activities;

    try {
      activities = userQueryRepository.collectUserActivityScores(period, baseDate);
    } catch (Exception e) {
      log.error("활동 데이터 수집 중 예외 발생: {}", e.getMessage(), e);
      throw new IllegalStateException("활동 데이터 수집 실패", e);
    }

    if (activities.isEmpty()) {
      log.info("점수 계산 대상 없음 - 저장 생략: period={}, baseDate={}", period, baseDate);
      return;
    }

    // 활동 대상 사용자들의 ID 추출
    List<UUID> userIds = activities.stream()
        .map(UserActivity::userId)
        .toList();

    // 사용자들을 Map으로 구성
    Map<UUID, User> userMap;
    try {
      userMap = userRepository.findAllById(userIds).stream()
          .collect(Collectors.toMap(User::getId, Function.identity()));
    } catch (Exception e) {
      log.error("사용자 조회 중 예외 발생: {}", e.getMessage(), e);
      throw new IllegalStateException("사용자 조회 실패", e);
    }

    Map<UUID, UserScore> userScoreMap;
    try {
      List<UserScore> savedScores = userScoreRepository.findAllByPeriodAndDate(period, baseDate);
      userScoreMap = savedScores.stream()
          .collect(Collectors.toMap(score -> score.getUser().getId(), Function.identity()));
    } catch (Exception e) {
      log.error("기존 점수 조회 중 예외 발생: {}", e.getMessage(), e);
      throw new IllegalStateException("기존 점수 조회 실패", e);
    }

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

      try {
        if (existing != null) {
          if (!existing.isSameScores(reviewScoreSum, likeCount, commentCount)) {
            existing.updateScores(reviewScoreSum, likeCount, commentCount);
            userScoreRepository.save(existing);
            updated++;
          } else {
            skipped++;
          }
        } else {
          UserScore newScore = UserScore.create(user, period, baseDate, reviewScoreSum, likeCount,
              commentCount);
          userScoreRepository.save(newScore);
          inserted++;
        }
      } catch (Exception e) {
        log.error("점수 저장 중 예외 발생 - userId={}, error={}", userId, e.getMessage(), e);
      }
    }

    log.info("유저 점수 저장 완료 - 신규: {}, 업데이트: {}, 생략: {}", inserted, updated, skipped);
  }

  @Override
  public void updateRanksForPeriodAndDate(Period period, LocalDate date) {
    log.info("파워 유저 랭킹 갱신 시작: period={}, date={}", period, date);

    List<UserScore> scores;
    try {
      scores = userScoreRepository.findAllByPeriodAndDateOrderByScoreDesc(period, date);
    } catch (Exception e) {
      log.error("유저 점수 조회 실패: period={}, date={}, error={}", period, date, e.getMessage(), e);
      throw new IllegalStateException("유저 점수 조회 중 오류 발생", e);
    }

    if (scores.isEmpty()) {
      log.warn("갱신할 유저 점수가 없음: period={}, date={}", period, date);
      return;
    }

    long rank = 1;
    for (UserScore score : scores) {
      try {
        score.updateRank(rank++);
      } catch (Exception e) {
        log.warn("랭킹 업데이트 실패 - userId={}, scoreId={}, error={}",
            score.getUser().getId(), score.getId(), e.getMessage(), e);
      }
    }


    try {
      userScoreRepository.saveAll(scores);
      log.info("파워 유저 랭킹 갱신 완료: 갱신된 유저 수={}", scores.size());
    } catch (Exception e) {
      log.error("파워 유저 랭킹 저장 실패: error={}", e.getMessage(), e);
      throw new IllegalStateException("랭킹 저장 중 오류 발생", e);
    }
  }

}
