package com.sprint.deokhugamteam7.domain.user.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.repository.BookRepository;
import com.sprint.deokhugamteam7.domain.comment.entity.Comment;
import com.sprint.deokhugamteam7.domain.comment.repository.CommentRepository;
import com.sprint.deokhugamteam7.domain.review.entity.RankingReview;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.review.entity.ReviewLike;
import com.sprint.deokhugamteam7.domain.review.repository.RankingReviewRepository;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewLikeRepository;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewRepository;
import com.sprint.deokhugamteam7.domain.user.dto.request.UserLoginRequest;
import com.sprint.deokhugamteam7.domain.user.dto.request.UserRegisterRequest;
import com.sprint.deokhugamteam7.domain.user.dto.request.UserUpdateRequest;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import com.sprint.deokhugamteam7.domain.user.service.PowerUserService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PowerUserService powerUserService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private RankingReviewRepository rankingReviewRepository;

  @Autowired
  private ReviewLikeRepository reviewLikeRepository;

  @Autowired
  private CommentRepository commentRepository;

  private User user;

  @BeforeEach
  void setUp() {
    String encryptedPassword = passwordEncoder.encode("Password1!");
    user = User.create("user@example.com", "tester", encryptedPassword);
    userRepository.save(user);
  }

  @Test
  @DisplayName("회원가입")
  void register() throws Exception {
    UserRegisterRequest request = new UserRegisterRequest("new@example.com", "newbie",
        "Password1!");

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value("new@example.com"));
  }

  @Test
  @DisplayName("로그인")
  void login() throws Exception {
    UserLoginRequest request = new UserLoginRequest("user@example.com", "Password1!");

    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("user@example.com"));
  }

  @Test
  @DisplayName("유저 상세 조회")
  void getUser() throws Exception {
    mockMvc.perform(get("/api/users/{id}", user.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nickname").value("tester"));
  }

  @Test
  @DisplayName("유저 닉네임 수정")
  void updateUser() throws Exception {
    UserUpdateRequest request = new UserUpdateRequest("updatedNickname");

    mockMvc.perform(patch("/api/users/{id}", user.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nickname").value("updatedNickname"));
  }

  @Test
  @DisplayName("유저 삭제 - 소프트")
  void softDeleteUser() throws Exception {
    mockMvc.perform(delete("/api/users/{id}", user.getId()))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("유저 삭제 - 하드")
  void hardDeleteUser() throws Exception {
    mockMvc.perform(delete("/api/users/{id}/hard", user.getId()))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("파워 유저 목록 조회")
  void getPowerUsers() throws Exception {
    // given
    Book book = Book.create("테스트 도서", "브루노 마스", "테스트 출판", LocalDate.of(2020, 1, 1)).build();
    bookRepository.save(book);

    User user2 = User.create("user2@example.com", "user2", passwordEncoder.encode("Password1!"));
    User user3 = User.create("user3@example.com", "user3", passwordEncoder.encode("Password1!"));
    userRepository.saveAll(List.of(user2, user3));

    Review review1 = Review.create(book, user, "리뷰1", 5);
    Review review2 = Review.create(book, user2, "리뷰2", 4);
    Review review3 = Review.create(book, user3, "리뷰3", 3);
    reviewRepository.saveAll(List.of(review1, review2, review3));

    // 랭킹 리뷰
    rankingReviewRepository.save(RankingReview.create(review1, 10.0, Period.DAILY));
    rankingReviewRepository.save(RankingReview.create(review2, 5.0, Period.DAILY));

    // 좋아요
    reviewLikeRepository.save(ReviewLike.create(user2, review1)); // user2 -> user1
    reviewLikeRepository.save(ReviewLike.create(user3, review1)); // user3 -> user1
    reviewLikeRepository.save(ReviewLike.create(user, review2));  // user -> user2

    // 댓글
    commentRepository.save(Comment.create(user2, review1, "good"));
    commentRepository.save(Comment.create(user3, review1, "nice"));
    commentRepository.save(Comment.create(user, review2, "cool"));

    // when - 점수 계산
    powerUserService.calculateAndSaveUserScores(Period.DAILY, LocalDate.now());

    // then - 파워 유저 조회
    mockMvc.perform(get("/api/users/power")
            .param("period", Period.DAILY.name())
            .param("size", "5")
            .param("direction", "DESC"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].userId").exists())
        .andExpect(jsonPath("$.content[0].score").exists())
        .andExpect(jsonPath("$.size").value(2))
        .andExpect(jsonPath("$.hasNext").value(false));
  }
}