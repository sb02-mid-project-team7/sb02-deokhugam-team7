package com.sprint.deokhugamteam7.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.user.dto.PowerUserSearchCondition;
import com.sprint.deokhugamteam7.domain.user.dto.UserActivity;
import com.sprint.deokhugamteam7.domain.user.dto.response.CursorPageResponsePowerUserDto;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.entity.UserScore;
import com.sprint.deokhugamteam7.domain.user.repository.UserQueryRepository;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import com.sprint.deokhugamteam7.domain.user.repository.UserScoreRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort.Direction;

@ExtendWith(MockitoExtension.class)
class PowerUserServiceTest {

  @InjectMocks
  private PowerUserServiceImpl powerUserService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserQueryRepository userQueryRepository;

  @Mock
  private UserScoreRepository userScoreRepository;

  LocalDate baseDate;

  @BeforeEach
  void setUp() {
    baseDate = LocalDate.now(ZoneId.of("Asia/Seoul"));
  }

  @Nested
  @DisplayName("파워 유저 목록 조회")
  class GetPowerUser {

    @Test
    @DisplayName("정상 조회")
    void getPowerUsers_success() {
      // given
      PowerUserSearchCondition condition = new PowerUserSearchCondition();
      condition.setPeriod(Period.DAILY);
      condition.setSize(1);
      condition.setDirection(Direction.valueOf("DESC"));
      condition.setCursor(null);
      condition.setAfter(null);

      User user = User.create("user@example.com", "nickname", "pw123!");
      UserScore userScore = UserScore.create(user, Period.DAILY, baseDate, 30.0, 5, 3);

      when(userQueryRepository.findPowerUserScoresByPeriod(condition))
          .thenReturn(List.of(userScore));
      when(userQueryRepository.countByCondition(condition))
          .thenReturn(1L);

      // when
      CursorPageResponsePowerUserDto result = powerUserService.getPowerUsers(condition);

      // then
      assertThat(result).isNotNull();
      assertThat(result.content()).hasSize(1);
      assertThat(result.totalElements()).isEqualTo(1);
      assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("조회 실패 시 예외 발생")
    void getPowerUsers_findFail_throwsException() {
      // given
      PowerUserSearchCondition condition = new PowerUserSearchCondition();
      condition.setPeriod(Period.DAILY);
      condition.setSize(1);
      when(userQueryRepository.findPowerUserScoresByPeriod(condition))
          .thenThrow(new RuntimeException("DB 오류"));

      // when & then
      Exception ex = assertThrows(IllegalStateException.class, () ->
          powerUserService.getPowerUsers(condition)
      );
      assertThat(ex.getMessage()).isEqualTo("파워 유저 조회 중 오류 발생");
    }
  }

  @Nested
  @DisplayName("유저 스코어 계산 및 저장")
  class CalculatorUserScore {

    @Test
    @DisplayName("점수 계산 및 저장 성공")
    void user_score_success() {
      // given
      Period period = Period.DAILY;

      UUID userId = UUID.randomUUID();
      User mockUser = mock(User.class);
      when(mockUser.getId()).thenReturn(userId);

      UserActivity activity = new UserActivity(userId, 30.0, 10L, 5L);

      when(userQueryRepository.collectUserActivityScores(period, baseDate))
          .thenReturn(List.of(activity));
      when(userRepository.findAllById(List.of(userId)))
          .thenReturn(List.of(mockUser));
      when(userScoreRepository.findAllByPeriodAndDate(period, baseDate))
          .thenReturn(List.of());

      // when
      powerUserService.calculateAndSaveUserScores(period, baseDate);

      // then
      verify(userScoreRepository, times(1)).save(any(UserScore.class));
    }

    @Test
    @DisplayName("활동 데이터 수집 실패")
    void fail_collectUserActivityScores() {
      // given
      when(userQueryRepository.collectUserActivityScores(any(), any()))
          .thenThrow(new RuntimeException("DB 실패"));

      // when
      Exception ex = assertThrows(IllegalStateException.class, () ->
          powerUserService.calculateAndSaveUserScores(Period.DAILY, baseDate)
      );

      // then
      assertEquals("활동 데이터 수집 실패", ex.getMessage());
    }

    @Test
    @DisplayName("사용자 조회 실패")
    void fail_userRepository() {
      // given
      UUID userId = UUID.randomUUID();
      UserActivity activity = new UserActivity(userId, 10.0, 1L, 2L);

      when(userQueryRepository.collectUserActivityScores(any(), any()))
          .thenReturn(List.of(activity));
      when(userRepository.findAllById(any()))
          .thenThrow(new RuntimeException("사용자 조회 실패"));

      // when
      Exception ex = assertThrows(IllegalStateException.class, () ->
          powerUserService.calculateAndSaveUserScores(Period.DAILY, baseDate)
      );

      // then
      assertEquals("사용자 조회 실패", ex.getMessage());
    }

    @Test
    @DisplayName("기존 점수 조회 실패")
    void fail_existingScoreQuery() {
      // given
      UUID userId = UUID.randomUUID();
      User mockUser = mock(User.class);
      when(mockUser.getId()).thenReturn(userId);
      UserActivity activity = new UserActivity(userId, 10.0, 1L, 1L);

      when(userQueryRepository.collectUserActivityScores(any(), any()))
          .thenReturn(List.of(activity));
      when(userRepository.findAllById(any()))
          .thenReturn(List.of(mockUser));
      when(userScoreRepository.findAllByPeriodAndDate(any(), any()))
          .thenThrow(new RuntimeException("기존 점수 조회 실패"));

      // when
      Exception ex = assertThrows(IllegalStateException.class, () ->
          powerUserService.calculateAndSaveUserScores(Period.DAILY, baseDate)
      );

      // then
      assertEquals("기존 점수 조회 실패", ex.getMessage());
    }
  }

  @Nested
  @DisplayName("랭크 업데이트")
  class Rank {

    @Test
    @DisplayName("랭크 업데이트 성공")
    void rank_success() {
      // given
      Period period = Period.WEEKLY;

      User mockUser = mock(User.class);

      UserScore s1 = UserScore.create(mockUser, period, baseDate, 20.0, 5, 5);
      UserScore s2 = UserScore.create(mockUser, period, baseDate, 15.0, 3, 2);
      List<UserScore> scores = List.of(s1, s2);

      when(userScoreRepository.findAllByPeriodAndDateOrderByScoreDesc(period, baseDate))
          .thenReturn(scores);

      // when
      powerUserService.updateRanksForPeriodAndDate(period, baseDate);

      // then
      assertEquals(1L, s1.getRank());
      assertEquals(2L, s2.getRank());
      verify(userScoreRepository).saveAll(scores);
    }

    @Test
    @DisplayName("점수 조회 실패")
    void fail_scoreQuery() {
      // given
      when(userScoreRepository.findAllByPeriodAndDateOrderByScoreDesc(any(), any()))
          .thenThrow(new RuntimeException("DB 조회 실패"));

      // when
      Exception ex = assertThrows(IllegalStateException.class, () ->
          powerUserService.updateRanksForPeriodAndDate(Period.WEEKLY, baseDate)
      );

      // then
      assertEquals("유저 점수 조회 중 오류 발생", ex.getMessage());
    }

    @Test
    @DisplayName("점수 저장 실패")
    void fail_scoreSave() {
      // given
      User mockUser = mock(User.class);
      UserScore s1 = UserScore.create(mockUser, Period.WEEKLY, baseDate, 10.0, 2, 1);
      UserScore s2 = UserScore.create(mockUser, Period.WEEKLY, baseDate, 5.0, 1, 1);
      List<UserScore> scores = List.of(s1, s2);

      when(userScoreRepository.findAllByPeriodAndDateOrderByScoreDesc(any(), any()))
          .thenReturn(scores);
      doThrow(new RuntimeException("저장 실패")).when(userScoreRepository).saveAll(scores);

      // when
      Exception ex = assertThrows(IllegalStateException.class, () ->
          powerUserService.updateRanksForPeriodAndDate(Period.WEEKLY, baseDate)
      );

      // then
      assertEquals("랭킹 저장 중 오류 발생", ex.getMessage());
    }
  }
}
