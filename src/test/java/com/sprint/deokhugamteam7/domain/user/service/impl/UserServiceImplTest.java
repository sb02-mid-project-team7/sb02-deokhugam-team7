package com.sprint.deokhugamteam7.domain.user.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.domain.user.dto.request.UserLoginRequest;
import com.sprint.deokhugamteam7.domain.user.dto.request.UserRegisterRequest;
import com.sprint.deokhugamteam7.domain.user.dto.response.UserDto;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import com.sprint.deokhugamteam7.exception.DeokhugamException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserServiceImpl userService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  @DisplayName("회원가입")
  class Register {

    @Test
    @DisplayName("성공")
    void register_success() {
      // given
      UserRegisterRequest request = new UserRegisterRequest("test@example.com", "tester", "Password123!");

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
      UserRegisterRequest request = new UserRegisterRequest("test@example.com", "tester", "Password123!");
      when(userRepository.existsByEmail(request.email())).thenReturn(true);

      Throwable thrown = catchThrowable(() -> userService.register(request));

      assertThat(thrown)
          .isInstanceOf(DeokhugamException.class)
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

      when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));

      UserDto result = userService.login(request);

      assertThat(result).isNotNull();
      assertThat(result.email()).isEqualTo(user.getEmail());
      assertThat(result.nickname()).isEqualTo(user.getNickname());
    }

    @Test
    @DisplayName("존재하지 않는 유저 예외")
    void login_userNotFound() {
      UserLoginRequest request = new UserLoginRequest("noone@example.com", "pw");
      when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

      Throwable thrown = catchThrowable(() -> userService.login(request));

      assertThat(thrown)
          .isInstanceOf(DeokhugamException.class)
          .hasMessage("Internal Server Error");
    }

    @Test
    @DisplayName("비밀번호 불일치 예외")
    void login_wrongPassword() {
      UserLoginRequest request = new UserLoginRequest("test@example.com", "wrongpassword");
      User user = User.create("test@example.com", "tester", "Password123!");

      when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));

      Throwable thrown = catchThrowable(() -> userService.login(request));

      assertThat(thrown)
          .isInstanceOf(DeokhugamException.class)
          .hasMessage("Internal Server Error");
    }
  }
}
