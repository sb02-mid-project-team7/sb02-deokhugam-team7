package com.sprint.deokhugamteam7.domain.user.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.user.dto.UserActivity;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.entity.UserScore;
import com.sprint.deokhugamteam7.domain.user.repository.UserQueryRepository;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import com.sprint.deokhugamteam7.domain.user.repository.UserScoreRepository;
import com.sprint.deokhugamteam7.domain.user.service.PowerUserServiceImpl;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PowerUserServiceImplTest {

  @InjectMocks
  private PowerUserServiceImpl powerUserService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserQueryRepository userQueryRepository;

  @Mock
  private UserScoreRepository userScoreRepository;

  @Nested
  @DisplayName("파워 유저")
  class PowerUser {

    @Test
    @DisplayName("유저 스코어 계산 및 저장")
    void user_score_success() {
      // given
      Period period = Period.DAILY;
      LocalDate baseDate = LocalDate.now(ZoneId.of("Asia/Seoul"));

      UUID userId = UUID.randomUUID();
      User mockUser = mock(User.class);
      when(mockUser.getId()).thenReturn(userId);

      UserActivity activity = new UserActivity(userId, 30.0, 10L, 5L);
      when(userQueryRepository.collectUserActivityScores(period, baseDate))
          .thenReturn(List.of(activity));
      when(userRepository.findAllById(List.of(userId)))
          .thenReturn(List.of(mockUser));

      // when
      powerUserService.calculateAndSaveUserScores(period, baseDate);

      // then
      verify(userScoreRepository, times(1)).deleteByPeriodAndDate(period, baseDate);
      verify(userScoreRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("랭크 업데이트")
    void rank_success() {
      // given
      Period period = Period.WEEKLY;
      LocalDate date = LocalDate.now();

      User mockUser = mock(User.class);

      UserScore s1 = UserScore.create(mockUser, period, date, 20.0, 5, 5);
      UserScore s2 = UserScore.create(mockUser, period, date, 15.0, 3, 2);
      List<UserScore> scores = List.of(s1, s2);

      when(userScoreRepository.findAllByPeriodAndDateOrderByScoreDesc(period, date))
          .thenReturn(scores);

      // when
      powerUserService.updateRanksForPeriodAndDate(period, date);

      // then
      assertEquals(1L, s1.getRank());
      assertEquals(2L, s2.getRank());
      verify(userScoreRepository).saveAll(scores);
    }
  }
}
