package com.sprint.deokhugamteam7.domain.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.deokhugamteam7.config.TestSecurityConfig;
import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.user.dto.request.UserLoginRequest;
import com.sprint.deokhugamteam7.domain.user.dto.request.UserRegisterRequest;
import com.sprint.deokhugamteam7.domain.user.dto.request.UserUpdateRequest;
import com.sprint.deokhugamteam7.domain.user.dto.response.CursorPageResponsePowerUserDto;
import com.sprint.deokhugamteam7.domain.user.dto.response.UserDto;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.service.PowerUserService;
import com.sprint.deokhugamteam7.domain.user.service.UserService;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import com.sprint.deokhugamteam7.exception.user.UserException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private PowerUserService powerUserService;

  private UUID userId;

  private LocalDateTime baseTime;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    baseTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
  }

  @Test
  @DisplayName("회원가입 성공")
  void registerTest() throws Exception {
    UserRegisterRequest request = new UserRegisterRequest("test@example.com", "tester", "Test123!");
    UserDto response = new UserDto(UUID.randomUUID(), "test@example.com", "tester", baseTime);

    Mockito.when(userService.register(Mockito.any())).thenReturn(response);

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value("test@example.com"));
  }

  @Test
  @DisplayName("회원가입 실패 - 유효성 검증 실패")
  void registerValidationFailTest() throws Exception {
    UserRegisterRequest invalidRequest = new UserRegisterRequest("invalid-email", "a", "123");

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(ErrorCode.METHOD_ARGUMENT_NOT_VALID.name()));
  }

  @Test
  @DisplayName("로그인 성공")
  void loginTest() throws Exception {
    UserLoginRequest request = new UserLoginRequest("test@example.com", "Test123!");
    UserDto response = new UserDto(userId, "test@example.com", "tester", baseTime);

    Mockito.when(userService.login(Mockito.any())).thenReturn(response);

    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.email").value("test@example.com"));
  }

  @Test
  @DisplayName("로그인 실패 - 잘못된 이메일 또는 비밀번호")
  void loginFailTest() throws Exception {
    UserLoginRequest request = new UserLoginRequest("wrong@example.com", "wrongpw");

    Mockito.when(userService.login(Mockito.any()))
        .thenThrow(new UserException(ErrorCode.LOGIN_FAILED));

    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value(ErrorCode.LOGIN_FAILED.name()));
  }

  @Test
  @DisplayName("유저 상세 조회")
  void findByIdTest() throws Exception {
    UserDto response = new UserDto(userId, "test@example.com", "tester", null);

    Mockito.when(userService.findById(userId)).thenReturn(response);

    mockMvc.perform(get("/api/users/" + userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()));
  }

  @Test
  @DisplayName("유저 조회 실패 - 존재하지 않는 ID")
  void findByIdNotFoundTest() throws Exception {
    UUID unknownId = UUID.randomUUID();

    Mockito.when(userService.findById(unknownId))
        .thenThrow(new UserException(ErrorCode.USER_NOT_FOUND));

    mockMvc.perform(get("/api/users/" + unknownId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.name()));
  }

  @Test
  @DisplayName("유저 닉네임 수정")
  void updateUserTest() throws Exception {
    UserUpdateRequest request = new UserUpdateRequest("updatedNick");
    UserDto response = new UserDto(userId, "test@example.com", "updatedNick", null);

    Mockito.when(userService.update(Mockito.eq(userId), Mockito.any())).thenReturn(response);

    mockMvc.perform(patch("/api/users/" + userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nickname").value("updatedNick"));
  }

  @Test
  @DisplayName("유저 논리 삭제")
  void softDeleteUserTest() throws Exception {
    mockMvc.perform(delete("/api/users/" + userId))
        .andExpect(status().isNoContent());

    Mockito.verify(userService).softDeleteById(userId);
  }

  @Test
  @DisplayName("유저 하드 삭제")
  void hardDeleteUserTest() throws Exception {
    mockMvc.perform(delete("/api/users/" + userId + "/hard"))
        .andExpect(status().isNoContent());

    Mockito.verify(userService).hardDeleteById(userId);
  }

  @Test
  @DisplayName("파워 유저 목록 조회")
  void getPowerUsersTest() throws Exception {
    CursorPageResponsePowerUserDto response = new CursorPageResponsePowerUserDto(
        Collections.emptyList(), null, null, 10, 0L, false
    );

    Mockito.when(powerUserService.getPowerUsers(Mockito.any())).thenReturn(response);

    mockMvc.perform(get("/api/users/power")
            .param("period", Period.DAILY.name())
            .param("size", "10")
            .param("direction", "DESC"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray());
  }
}
