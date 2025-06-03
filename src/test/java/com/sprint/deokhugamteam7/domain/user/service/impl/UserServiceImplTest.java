package com.sprint.deokhugamteam7.domain.user.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.domain.user.dto.request.UserLoginRequest;
import com.sprint.deokhugamteam7.domain.user.dto.request.UserRegisterRequest;
import com.sprint.deokhugamteam7.domain.user.dto.request.UserUpdateRequest;
import com.sprint.deokhugamteam7.domain.user.dto.response.UserDto;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import com.sprint.deokhugamteam7.exception.DeokhugamException;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import com.sprint.deokhugamteam7.exception.user.UserException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserServiceImpl userService;

  @Nested
  @DisplayName("회원가입")
  class Register {

    @Test
    @DisplayName("성공")
    void register_success() {
      // given
      UserRegisterRequest request = new UserRegisterRequest("test@example.com", "tester",
          "Password123!");

      when(userRepository.existsByEmail(request.email())).thenReturn(false);
      when(userRepository.save(any(User.class))).thenReturn(
          User.create(request.email(), request.nickname(), request.password())
      );

      // when
      UserDto result = userService.register(request);

      // then
      assertThat(result.email()).isEqualTo(request.email());
      assertThat(result.nickname()).isEqualTo(request.nickname());
    }

    @Test
    @DisplayName("중복 이메일 예외")
    void register_duplicateEmail() {
      UserRegisterRequest request = new UserRegisterRequest("test@example.com", "tester",
          "Password123!");
      when(userRepository.existsByEmail(request.email())).thenReturn(true);

      Throwable thrown = catchThrowable(() -> userService.register(request));

      assertThat(thrown)
          .isInstanceOf(UserException.class)
          .hasMessage("Internal Server Error");
    }
  }

  @Nested
  @DisplayName("로그인")
  class Login {

    @Test
    @DisplayName("성공")
    void login_success() {
      UserLoginRequest request = new UserLoginRequest("test@example.com", "Password123!");
      User user = User.create("test@example.com", "tester", "Password123!");

      when(userRepository.findByEmailIsDeletedFalse(request.email())).thenReturn(Optional.of(user));

      UserDto result = userService.login(request);

      assertThat(result).isNotNull();
      assertThat(result.email()).isEqualTo(user.getEmail());
      assertThat(result.nickname()).isEqualTo(user.getNickname());
    }

    @Test
    @DisplayName("존재하지 않는 유저 예외")
    void login_userNotFound() {
      UserLoginRequest request = new UserLoginRequest("noone@example.com", "pw");
      when(userRepository.findByEmailIsDeletedFalse(request.email())).thenReturn(Optional.empty());

      Throwable thrown = catchThrowable(() -> userService.login(request));

      assertThat(thrown)
          .isInstanceOf(UserException.class)
          .hasMessage("Internal Server Error");
    }

    @Test
    @DisplayName("비밀번호 불일치 예외")
    void login_wrongPassword() {
      UserLoginRequest request = new UserLoginRequest("test@example.com", "wrongpassword");
      User user = User.create("test@example.com", "tester", "Password123!");

      when(userRepository.findByEmailIsDeletedFalse(request.email())).thenReturn(Optional.of(user));

      Throwable thrown = catchThrowable(() -> userService.login(request));

      assertThat(thrown)
          .isInstanceOf(UserException.class)
          .hasMessage("Internal Server Error");
    }
  }

  @Nested
  @DisplayName("조회")
  class FindById {

    @Test
    @DisplayName("조회 성공")
    void findById_success() {
      User user = User.create("test@example.com", "tester", "Password123!");
      when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

      UserDto result = userService.findById(user.getId());
      assertThat(result).isNotNull();
      assertThat(result.email()).isEqualTo(user.getEmail());
      assertThat(result.nickname()).isEqualTo(user.getNickname());
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 조회 실패")
    void findById_userNotFound() {
      UUID userId = UUID.randomUUID();
      // given
      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> userService.findById(userId))
          .isInstanceOf(UserException.class)
          .hasMessageContaining(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    }
  }

  @Nested
  @DisplayName("수정")
  class Update {

    @Test
    @DisplayName("사용자 닉네임 변경 성공")
    void update_success() {
      // given
      UUID userId = UUID.randomUUID();
      User user = User.create("test@example.com", "oldNick", "password123!");
      when(userRepository.findById(userId)).thenReturn(Optional.of(user));

      UserUpdateRequest request = new UserUpdateRequest("newNick");

      // when
      UserDto updatedUser = userService.update(userId, request);

      // then
      assertThat(updatedUser.nickname()).isEqualTo("newNick");
      assertThat(updatedUser.email()).isEqualTo("test@example.com");
      verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 수정")
    void update_userNotFound() {
      // given
      UUID userId = UUID.randomUUID();
      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      UserUpdateRequest request = new UserUpdateRequest("anyNick");

      // when & then
      assertThatThrownBy(() -> userService.update(userId, request))
          .isInstanceOf(UserException.class)
          .hasMessageContaining(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());

      verify(userRepository).findById(userId);
    }
  }

  @Nested
  @DisplayName("논리 삭제")
  class SoftDelete {
    @Test
    @DisplayName("soft delete 성공")
    void softDelete_success() {
      // given
      UUID userId = UUID.randomUUID();
      User user = User.create("soft@example.com", "softie", "pw123!");
      when(userRepository.findById(userId)).thenReturn(Optional.of(user));

      // when
      userService.softDeleteById(userId);

      // then
      assertThat(user.isDeleted()).isTrue();
      verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("soft delete 시 사용자가 없으면 예외 발생")
    void softDelete_userNotFound() {
      // given
      UUID userId = UUID.randomUUID();
      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> userService.softDeleteById(userId))
          .isInstanceOf(DeokhugamException.class)
          .hasMessageContaining(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());

      verify(userRepository).findById(userId);
    }
  }

  @Nested
  @DisplayName("물리 삭제")
  class HardDelete {
    @Test
    @DisplayName("hard delete 성공")
    void hardDelete_success() {
      // given
      UUID userId = UUID.randomUUID();
      when(userRepository.existsById(userId)).thenReturn(true);

      // when
      userService.hardDeleteById(userId);

      // then
      verify(userRepository).deleteById(userId);
    }

    @Test
    @DisplayName("hard delete 시 사용자가 없으면 예외 발생")
    void hardDelete_userNotFound() {
      // given
      UUID userId = UUID.randomUUID();
      when(userRepository.existsById(userId)).thenReturn(false);

      // when & then
      assertThatThrownBy(() -> userService.hardDeleteById(userId))
          .isInstanceOf(DeokhugamException.class)
          .hasMessageContaining(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());

      verify(userRepository).existsById(userId);
    }
  }
}
