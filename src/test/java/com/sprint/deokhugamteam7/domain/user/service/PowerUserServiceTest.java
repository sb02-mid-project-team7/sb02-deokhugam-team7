package com.sprint.deokhugamteam7.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
      UserScore userScore = UserScore.create(user, Period.DAILY, 30.0, 5, 3);

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
      when(userScoreRepository.findAllByPeriod(period))
          .thenReturn(List.of());

      // when
      powerUserService.calculateAndSaveUserScores(period, baseDate);

      // then
      verify(userScoreRepository, times(1)).save(any(UserScore.class));
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

      UserScore s1 = UserScore.create(mockUser, period, 20.0, 5, 5);
      UserScore s2 = UserScore.create(mockUser, period, 15.0, 3, 2);
      List<UserScore> scores = List.of(s1, s2);

      when(userScoreRepository.findAllByPeriodOrderByScoreDesc(period))
          .thenReturn(scores);

      // when
      powerUserService.updateRanksForPeriod(period);

      // then
      assertEquals(1L, s1.getRank());
      assertEquals(2L, s2.getRank());
      verify(userScoreRepository).saveAll(scores);
    }
  }
}
