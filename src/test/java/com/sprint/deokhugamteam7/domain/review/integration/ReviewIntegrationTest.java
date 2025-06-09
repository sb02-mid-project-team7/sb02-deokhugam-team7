package com.sprint.deokhugamteam7.domain.review.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.repository.BookRepository;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewCreateRequest;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewUpdateRequest;
import com.sprint.deokhugamteam7.domain.review.dto.response.ReviewDto;
import com.sprint.deokhugamteam7.domain.review.service.PopularReviewScoreSchedule;
import com.sprint.deokhugamteam7.domain.review.service.ReviewService;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ReviewIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ReviewService reviewService;

  @Autowired
  private PopularReviewScoreSchedule popularReviewScoreSchedule;

  private User user;
  private Book book;
  private ReviewCreateRequest createRequest;
  private UUID userId;

  @BeforeEach
  void setUp() {
    user = User.create("test@gmail.com", "test1", "test1234!");
    userRepository.save(user);
    userId = user.getId();

    book = Book.create(
        "도메인 주도 설계", "에릭 에반스", "한빛미디어",
        LocalDate.of(2020, 1, 15)
    ).build();
    bookRepository.save(book);

    createRequest = new ReviewCreateRequest(book.getId(), userId, "리뷰1", 3);
  }

  @Test
  @DisplayName("리뷰 생성")
  void createReview_ShouldReturnReviewDto() throws Exception {
    mockMvc.perform(post("/api/reviews")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.content").value("리뷰1"))
        .andExpect(jsonPath("$.rating").value(3));
  }

  @Test
  @DisplayName("리뷰 수정")
  void updateReview_ShouldReturnReviewDto() throws Exception {
    ReviewDto reviewDto = reviewService.create(createRequest);
    book.setReviews(new ArrayList<>());
    ReviewUpdateRequest updateRequest = new ReviewUpdateRequest("수정", 5);

    mockMvc.perform(patch("/api/reviews/{reviewId}", reviewDto.id())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Deokhugam-Request-User-ID", userId)
            .content(objectMapper.writeValueAsString(updateRequest))
            .with(csrf())
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.content").value("수정"))
        .andExpect(jsonPath("$.rating").value(5));
  }

  @Test
  @DisplayName("논리적 삭제")
  void deleteSoftReview_Success() throws Exception {
    ReviewDto reviewDto = reviewService.create(createRequest);
    book.setReviews(new ArrayList<>());

    mockMvc.perform(delete("/api/reviews/{reviewId}", reviewDto.id())
            .header("Deokhugam-Request-User-ID", userId)
            .with(csrf()))
        .andExpect(status().isNoContent())
        .andDo(print());
  }

  @Test
  @DisplayName("물리적 삭제")
  void deleteHardReview_Success() throws Exception {
    ReviewDto reviewDto = reviewService.create(createRequest);

    mockMvc.perform(delete("/api/reviews/{reviewId}/hard", reviewDto.id())
            .header("Deokhugam-Request-User-ID", userId)
            .with(csrf()))
        .andExpect(status().isNoContent())
        .andDo(print());
  }

  @Test
  @DisplayName("단건 조회")
  void findReviewById_ShouldReturnReviewDto() throws Exception {
    ReviewDto reviewDto = reviewService.create(createRequest);

    mockMvc.perform(get("/api/reviews/{reviewId}", reviewDto.id())
            .header("Deokhugam-Request-User-ID", userId)
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.content").value("리뷰1"))
        .andExpect(jsonPath("$.rating").value(3))
        .andDo(print());
  }

  @Test
  @DisplayName("리뷰 좋아요")
  void likeReview_Success() throws Exception {
    ReviewDto reviewDto = reviewService.create(createRequest);

    mockMvc.perform(post("/api/reviews/{reviewId}/like", reviewDto.id())
            .header("Deokhugam-Request-User-ID", userId)
            .with(csrf()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.reviewId").exists())
        .andExpect(jsonPath("$.userId").value(userId.toString()))
        .andExpect(jsonPath("$.liked").value(true))
        .andDo(print());
  }

  @Test
  @DisplayName("리뷰 목록 조회")
  void findAllReviews_ShouldReturnCursorPageResponseReviewDto() throws Exception {
    User user2 = User.create("test2@gmail.com", "test2", "test1234!");
    userRepository.save(user2);
    User user3 = User.create("test3@gmail.com", "test3", "test1234!");
    userRepository.save(user3);
    ReviewCreateRequest createRequest2 = new ReviewCreateRequest(book.getId(), user2.getId(), "리뷰2",
        4);
    ReviewCreateRequest createRequest3 = new ReviewCreateRequest(book.getId(), user3.getId(), "리뷰3",
        5);

    reviewService.create(createRequest);
    ReviewDto reviewDto2 = reviewService.create(createRequest2);
    reviewService.create(createRequest3);

    mockMvc.perform(get("/api/reviews")
            .param("limit", "2")
            .header("Deokhugam-Request-User-ID", userId)
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.nextCursor").value(reviewDto2.createdAt().toString()))
        .andExpect(jsonPath("$.size").value(2))
        .andExpect(jsonPath("$.totalElements").value(3))
        .andExpect(jsonPath("$.hasNext").value(true))
        .andDo(print());
  }

  @Test
  @DisplayName("리뷰 인기 목록 조회")
  void popularReviews_ShouldReturnCursorPageResponseReviewDto() throws Exception {
    User user2 = User.create("test2@gmail.com", "test2", "test1234!");
    userRepository.save(user2);
    User user3 = User.create("test3@gmail.com", "test3", "test1234!");
    userRepository.save(user3);
    ReviewCreateRequest createRequest2 = new ReviewCreateRequest(book.getId(), user2.getId(), "리뷰2",
        4);
    ReviewCreateRequest createRequest3 = new ReviewCreateRequest(book.getId(), user3.getId(), "리뷰3",
        5);

    ReviewDto reviewDto1 = reviewService.create(createRequest);
    ReviewDto reviewDto2 = reviewService.create(createRequest2);
    reviewService.create(createRequest3);

    reviewService.like(reviewDto1.id(), userId);
    reviewService.like(reviewDto1.id(), user2.getId());
    reviewService.like(reviewDto2.id(), userId);

    popularReviewScoreSchedule.scheduleScore();

    mockMvc.perform(get("/api/reviews/popular")
            .param("limit", "2")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].reviewId").value(reviewDto1.id().toString()))
        .andExpect(jsonPath("$.content[1].reviewId").value(reviewDto2.id().toString()))
        .andExpect(jsonPath("$.nextCursor").value((Object) null))
        .andExpect(jsonPath("$.size").value(2))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.hasNext").value(false))
        .andDo(print());
  }
}
