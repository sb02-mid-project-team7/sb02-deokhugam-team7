package com.sprint.deokhugamteam7.domain.user.entity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserTest {

  @Test
  @DisplayName("유저 생성 테스트")
  void createUser() {
    // given
    String email = "user@example.com";
    String nickname = "user";
    String password = "password";

    // when
    User user = User.create(email, nickname, password);

    // then
    assertThat(user.getEmail()).isEqualTo(email);
    assertThat(user.getNickname()).isEqualTo(nickname);
    assertThat(user.getPassword()).isEqualTo(password);
    assertThat(user.isDeleted()).isFalse(); // 기본값 확인
  }

  @Test
  @DisplayName("닉네임 수정 테스트")
  void updateNickname() {
    User user = User.create("a@a.com", "oldNick", "pw");
    user.update("newNick");

    assertThat(user.getNickname()).isEqualTo("newNick");
  }

  @Test
  @DisplayName("논리 삭제 테스트")
  void softDelete() {
    User user = User.create("a@a.com", "nick", "pw");
    user.softDelete();

    assertThat(user.isDeleted()).isTrue();
  }
}
