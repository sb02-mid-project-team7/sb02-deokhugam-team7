package com.sprint.deokhugamteam7.domain.user.entity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.sprint.deokhugamteam7.constant.Period;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserScoreTest {

  @Test
  @DisplayName("UserScore 생성 및 점수 계산 테스트")
  void createUserScore() {
    // given
    User user = User.create("user@example.com", "nick", "pw");
    LocalDate date = LocalDate.of(2024, 6, 9);

    // when
    UserScore score = UserScore.create(user, Period.DAILY,10.0, 5, 2);

    // then
    assertThat(score.getUser()).isEqualTo(user);
    assertThat(score.getPeriod()).isEqualTo(Period.DAILY);
    assertThat(score.getReviewScoreSum()).isEqualTo(10.0);
    assertThat(score.getLikeCount()).isEqualTo(5);
    assertThat(score.getCommentCount()).isEqualTo(2);
    assertThat(score.getScore()).isEqualTo(10.0 * 0.5 + 5 * 0.2 + 2 * 0.3);
  }

  @Test
  @DisplayName("UserScore 업데이트 테스트")
  void updateScoreTest() {
    User user = User.create("test@example.com", "nick", "pw");
    UserScore score = UserScore.create(user, Period.WEEKLY, 5.0, 2, 1);

    score.updateScores(20.0, 10, 5);

    assertThat(score.getReviewScoreSum()).isEqualTo(20.0);
    assertThat(score.getLikeCount()).isEqualTo(10);
    assertThat(score.getCommentCount()).isEqualTo(5);
    assertThat(score.getScore()).isEqualTo(20.0 * 0.5 + 10 * 0.2 + 5 * 0.3);
  }

  @Test
  @DisplayName("isSameScores가 true를 반환해야 하는 경우")
  void isSameScores_true() {
    User user = User.create("a@a.com", "nick", "pw");
    UserScore score = UserScore.create(user, Period.MONTHLY, 10.0, 3, 1);

    assertThat(score.isSameScores(10.0, 3, 1)).isTrue();
  }

  @Test
  @DisplayName("isSameScores가 false를 반환해야 하는 경우")
  void isSameScores_false() {
    User user = User.create("a@a.com", "nick", "pw");
    UserScore score = UserScore.create(user, Period.MONTHLY, 10.0, 3, 1);

    assertThat(score.isSameScores(20.0, 3, 1)).isFalse();
  }

  @Test
  @DisplayName("랭킹 업데이트 테스트")
  void updateRank() {
    User user = User.create("a@a.com", "nick", "pw");
    UserScore score = UserScore.create(user, Period.DAILY, 5.0, 1, 1);

    score.updateRank(7L);

    assertThat(score.getRank()).isEqualTo(7L);
  }
}
