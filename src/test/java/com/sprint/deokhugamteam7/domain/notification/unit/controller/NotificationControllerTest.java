package com.sprint.deokhugamteam7.domain.notification.unit.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.deokhugamteam7.domain.notification.controller.NotificationController;
import com.sprint.deokhugamteam7.domain.notification.dto.NotificationUpdateRequest;
import com.sprint.deokhugamteam7.domain.notification.service.NotificationService;
import com.sprint.deokhugamteam7.domain.user.service.UserService;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@WithMockUser
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = NotificationController.class)
class NotificationControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  private NotificationService notificationService;

  @MockitoBean
  private UserService userService;

  @Autowired
  private ObjectMapper objectMapper;

  private UUID userId;
  private UUID notificationId;
  private NotificationUpdateRequest notificationUpdateRequest;

  @BeforeEach
  void setUp() {
    userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    notificationId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    notificationUpdateRequest = new NotificationUpdateRequest(true);
  }

  @Test
  @DisplayName("알람 수정 시 NotificationId 타입 변환 에러 - 400")
  void notificationUpdateTypeMatchError() throws Exception {
    mockMvc.perform(patch("/api/notifications/{notificationId}", "error")
        .header("Deokhugam-Request-User-ID", userId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(notificationUpdateRequest))
        .with(csrf())
      )
      .andExpect(status().isBadRequest())
      .andDo(print());
  }

  @Test
  @DisplayName("알람 수정 시 Deokhugam-Request-User-ID 누락 에러 - 400")
  void notificationUpdateRequestParameterError() throws Exception {
    mockMvc.perform(patch("/api/notifications/{notificationId}", notificationId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(notificationUpdateRequest))
        .with(csrf())
      )
      .andExpect(status().isBadRequest())
      .andDo(print());
  }

  @Test
  @DisplayName("알림 수정 시 NotificationUpdateRequest 누락 에러 - 400")
  void NotificationUpdateRequest() throws Exception {
    mockMvc.perform(patch("/api/notifications/{notificationId}", notificationId)
        .header("Deokhugam-Request-User-ID", userId)
        .contentType(MediaType.APPLICATION_JSON)
        .with(csrf())
      )
      .andExpect(status().isBadRequest())
      .andDo(print());
  }

  @Test
  @DisplayName("알림 목록 수정 시 Deokhugam-Request-User-ID 누락 에러 - 400")
  void notificationUpdateRequestParameter() throws Exception {
    mockMvc.perform(patch("/api/notifications/read-all")
        .contentType(MediaType.APPLICATION_JSON)
        .with(csrf())
      )
      .andExpect(status().isBadRequest())
      .andDo(print());
  }

  @Test
  @DisplayName("알람 목록 조회 시 after 누락 에러(cursor 존재) - 400")
  void notificationFindAllCursorAssertError() throws Exception {
    mockMvc.perform(get("/api/notifications")
        .contentType(MediaType.APPLICATION_JSON)
        .param("userId", UUID.randomUUID().toString())
        .param("cursor", LocalDateTime.now().toString())
        .param("after", "")
        .with(csrf()))
      .andExpect(status().isBadRequest())
      .andDo(print());
  }

}