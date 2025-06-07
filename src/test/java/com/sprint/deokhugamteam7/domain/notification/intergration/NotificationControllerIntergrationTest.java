package com.sprint.deokhugamteam7.domain.notification.intergration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.repository.BookRepository;
import com.sprint.deokhugamteam7.domain.notification.dto.NotificationUpdateRequest;
import com.sprint.deokhugamteam7.domain.notification.entity.Notification;
import com.sprint.deokhugamteam7.domain.notification.repository.NotificationRepository;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewRepository;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import com.sprint.deokhugamteam7.exception.notification.NotificationException;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class NotificationControllerIntergrationTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private EntityManager entityManager;

  @Autowired
  private NotificationRepository notificationRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private BookRepository bookRepository;
  @Autowired
  private ReviewRepository reviewRepository;

  private User reviewer;
  private User otherUser;
  private Book book;
  private Review review;
  private List<Notification> notificationList = new ArrayList<>();


  @BeforeEach
  void setUp() {
    notificationRepository.deleteAll();
    reviewRepository.deleteAll();
    bookRepository.deleteAll();
    userRepository.deleteAll();

    reviewer = User.create("reviewer.test.com", "reviewer", "1234");
    otherUser = User.create("otherUser.test.com", "otherUser", "1234");
    userRepository.save(reviewer);
    userRepository.save(otherUser);

    book = Book.create("test book", "tester", "test", LocalDate.now()).build();
    bookRepository.save(book);

    review = Review.create(book, reviewer, "리뷰 테스트", 5);
    reviewRepository.save(review);

    // 30개 demo data 생성
    for (int i = 0; i < 30; i++) {
      Notification notification = Notification.create(reviewer, review, "리뷰 테스트: " + i);
      notificationRepository.save(notification);
      notificationList.add(notification);
    }
  }

  @Test
  @DisplayName("알림 단일 수정 정상 진행")
  void update_success() throws Exception {
    UUID notificationId = notificationList.get(0).getId();
    UUID userId = reviewer.getId();
    NotificationUpdateRequest request = new NotificationUpdateRequest(true);

    mockMvc.perform(patch("/api/notifications/{notificationId}", notificationId)
        .header("Deokhugam-Request-User-ID", userId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(notificationId.toString()))
      .andExpect(jsonPath("$.confirmed").value(true))
      .andDo(print());

    Notification updatedNotification = notificationRepository.findById(notificationId)
      .orElseThrow(() -> new NotificationException(ErrorCode.NOTIFICATION_NOT_FOUND));

    assertThat(updatedNotification.getConfirmed()).isTrue();
  }

  @Test
  @DisplayName("알림 목록 전체 수정 정상 진행")
  void update_all_success() throws Exception {
    UUID userId = reviewer.getId();

    mockMvc.perform(patch("/api/notifications/read-all")
      .header("Deokhugam-Request-User-ID", userId))
      .andExpect(status().isNoContent())
      .andDo(print());

    // 벌크 연산으로 인한 1차 캐시와 실제 데이터간의 불일치 해결을 위한 영속성 컨텍스트 초기화
    entityManager.flush();
    entityManager.clear();

    List<Notification> notificationList = notificationRepository.findByUserId(userId);

    assertThat(notificationList).isNotEmpty();
    assertThat(notificationList.stream().allMatch(Notification::getConfirmed)).isTrue();
  }

  @Test
  @DisplayName("알림 커서 기반 목록 조회 정상 진행")
  void findAll_success() throws Exception {
    UUID userId = reviewer.getId();

    mockMvc.perform(get("/api/notifications")
        .param("userId", userId.toString()))
      .andExpect(status().isOk())
      // 20개의 항목중 가장 나중에 불러온 항목의 시간값과 비교
      .andExpect(jsonPath("$.nextCursor").value(notificationList.get(10).getCreated_at().toString()))
      .andExpect(jsonPath("$.totalElements").value(30))
      .andExpect(jsonPath("$.hasNext").value(true))
      .andDo(print());
  }

  @Test
  @DisplayName("알림 커서 기반 목록 조회 hasNext: false")
  void findAll_success_hasNext_false() throws Exception {
    UUID userId = reviewer.getId();

    mockMvc.perform(get("/api/notifications")
        .param("userId", userId.toString())
        .param("limit", "30"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.totalElements").value(30))
      .andExpect(jsonPath("$.hasNext").value(false))
      .andDo(print());
  }
}
