package com.sprint.deokhugamteam7.domain.review.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.deokhugamteam7.config.TestSecurityConfig;
import com.sprint.deokhugamteam7.domain.review.dto.request.RankingReviewRequest;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewCreateRequest;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewSearchCondition;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewUpdateRequest;
import com.sprint.deokhugamteam7.domain.review.dto.response.CursorPageResponsePopularReviewDto;
import com.sprint.deokhugamteam7.domain.review.dto.response.CursorPageResponseReviewDto;
import com.sprint.deokhugamteam7.domain.review.dto.response.PopularReviewDto;
import com.sprint.deokhugamteam7.domain.review.dto.response.ReviewDto;
import com.sprint.deokhugamteam7.domain.review.dto.response.ReviewLikeDto;
import com.sprint.deokhugamteam7.domain.review.service.ReviewService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WithMockUser
@Import(TestSecurityConfig.class)
@WebMvcTest(controllers = ReviewController.class)
public class ReviewControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ReviewService reviewService;

  private final UUID userId = UUID.fromString("87654321-1234-5678-9123-123456789012");
  private final UUID reviewId = UUID.fromString("12345678-1234-1234-1234-123456789012");
  private final UUID bookId = UUID.fromString("91234567-1234-5678-1234-123456789012");
  private ReviewCreateRequest createRequest;
  private ReviewDto reviewDto;

  @BeforeEach
  void setUp() {
    createRequest = new ReviewCreateRequest(bookId, userId, "리뷰1", 3);
    reviewDto = ReviewDto.builder()
        .id(reviewId)
        .content("리뷰1")
        .rating(3)
        .build();
  }

  @Test
  @DisplayName("리뷰 생성 - 성공")
  void createReview_Success() throws Exception {
    when(reviewService.create(createRequest)).thenReturn(reviewDto);

    mockMvc.perform(post("/api/reviews")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(reviewId.toString()))
        .andExpect(jsonPath("$.content").value("리뷰1"))
        .andExpect(jsonPath("$.rating").value(3));
  }

  @Test
  @DisplayName("리뷰 생성 - 실패 : 요청의 rating 값이 0 ~ 5범위를 초과한 경우 [400]")
  void createReview_Fail() throws Exception {
    ReviewCreateRequest request_fail = new ReviewCreateRequest(bookId, userId, "리뷰1", 10);

    mockMvc.perform(post("/api/reviews")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request_fail))
        )
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("리뷰 수정 - 성공")
  void updateReview_Success() throws Exception {
    ReviewUpdateRequest request = new ReviewUpdateRequest("리뷰1", 3);
    when(reviewService.update(eq(reviewId), eq(userId), any(ReviewUpdateRequest.class)))
        .thenReturn(reviewDto);

    mockMvc.perform(patch("/api/reviews/{reviewId}", reviewId)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Deokhugam-Request-User-ID", userId.toString())
            .content(objectMapper.writeValueAsString(request))
            .with(csrf())
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(reviewId.toString()))
        .andExpect(jsonPath("$.content").value("리뷰1"))
        .andExpect(jsonPath("$.rating").value(3))
        .andDo(print());
  }

  @Test
  @DisplayName("리뷰 수정 - 실패: content 공백 [400]")
  void updateReview_Fail() throws Exception {
    ReviewUpdateRequest request = new ReviewUpdateRequest("", 3);
    when(reviewService.update(eq(reviewId), eq(userId), any(ReviewUpdateRequest.class)))
        .thenReturn(reviewDto);

    mockMvc.perform(patch("/api/reviews/{reviewId}", reviewId)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Deokhugam-Request-User-ID", userId.toString())
            .content(objectMapper.writeValueAsString(request))
            .with(csrf())
        )
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("리뷰 논리적 삭제 - 성공")
  void deleteSoftReview_Success() throws Exception {
    doNothing().when(reviewService).deleteSoft(reviewId, userId);

    mockMvc.perform(delete("/api/reviews/{reviewId}", reviewId)
            .header("Deokhugam-Request-User-ID", userId)
            .with(csrf()))
        .andExpect(status().isNoContent())
        .andDo(print());

    verify(reviewService).deleteSoft(reviewId, userId);
  }

  @Test
  @DisplayName("리뷰 물리적 삭제 - 성공")
  void deleteHardReview_Success() throws Exception {
    doNothing().when(reviewService).deleteHard(reviewId, userId);

    mockMvc.perform(delete("/api/reviews/{reviewId}/hard", reviewId)
            .header("Deokhugam-Request-User-ID", userId)
            .with(csrf()))
        .andExpect(status().isNoContent())
        .andDo(print());

    verify(reviewService).deleteHard(reviewId, userId);
  }

  @Test
  @DisplayName("리뷰 삭제 - 실패 : 요청자 누락 [400]")
  void deleteReview_Fail() throws Exception {
    mockMvc.perform(delete("/api/reviews/{reviewId}/hard", reviewId))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("리뷰 단건 조회 - 성공")
  void findReviewById_Success() throws Exception {
    when(reviewService.findById(reviewId, userId)).thenReturn(reviewDto);

    mockMvc.perform(get("/api/reviews/{reviewId}", reviewId)
            .header("Deokhugam-Request-User-ID", userId)
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(reviewId.toString()))
        .andExpect(jsonPath("$.content").value("리뷰1"))
        .andExpect(jsonPath("$.rating").value(3))
        .andDo(print());

    verify(reviewService).findById(reviewId, userId);
  }

  @Test
  @DisplayName("리뷰 좋아요 - 성공")
  void likeReview_Success() throws Exception {
    ReviewLikeDto likeDto = new ReviewLikeDto(reviewId, userId, true);

    when(reviewService.like(reviewId, userId)).thenReturn(likeDto);

    mockMvc.perform(post("/api/reviews/{reviewId}/like", reviewId)
            .header("Deokhugam-Request-User-ID", userId)
            .with(csrf()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.reviewId").value(reviewId.toString()))
        .andExpect(jsonPath("$.userId").value(userId.toString()))
        .andExpect(jsonPath("$.liked").value(true))
        .andDo(print());

    verify(reviewService).like(reviewId, userId);
  }

  @Test
  @DisplayName("리뷰 좋아요 - 실패: reviewId UUID 변환 실패 [400]")
  void likeReview_InvalidReviewIdFormat_Fail() throws Exception {
    mockMvc.perform(post("/api/reviews/{reviewId}/like", "error")
            .header("Deokhugam-Request-User-ID", userId.toString())
            .with(csrf()))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("리뷰 목록 조회 - 성공")
  void findAllReviews_Success() throws Exception {
    ReviewDto review1 = ReviewDto.builder()
        .id(UUID.randomUUID())
        .bookId(UUID.randomUUID())
        .build();

    ReviewDto review2 = ReviewDto.builder()
        .id(UUID.randomUUID())
        .bookId(UUID.randomUUID())
        .build();

    CursorPageResponseReviewDto responseDto = new CursorPageResponseReviewDto(
        List.of(review1, review2),
        "2",
        LocalDateTime.of(2025, 6, 2, 0, 0),
        2,
        2,
        false
    );

    when(reviewService.findAll(any(ReviewSearchCondition.class), eq(userId)))
        .thenReturn(responseDto);

    mockMvc.perform(get("/api/reviews")
            .param("sortBy", "createdAt")
            .param("direction", "DESC")
            .param("cursor", "2025-06-01T00:00:00")
            .header("Deokhugam-Request-User-ID", userId)
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.nextCursor").value("2"))
        .andExpect(jsonPath("$.size").value(2))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.hasNext").value(false))
        .andDo(print());

    verify(reviewService).findAll(any(ReviewSearchCondition.class), eq(userId));
  }

  @Test
  @DisplayName("리뷰 목록 조회 - 실패: 요청자 누락 [400]")
  void findAllReviews_Fail() throws Exception {
    mockMvc.perform(get("/api/reviews")
            .param("sortBy", "createdAt")
            .param("direction", "DESC")
            .param("cursor", "2025-06-01T00:00:00")
            .with(csrf()))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("인기 리뷰 목록 조회 - 성공")
  void getPopularReviews_Success() throws Exception {
    UUID reviewId1 = UUID.randomUUID();
    UUID reviewId2 = UUID.randomUUID();

    PopularReviewDto popular1 = PopularReviewDto.builder()
        .id(UUID.randomUUID())
        .reviewId(reviewId1)
        .build();
    PopularReviewDto popular2 = PopularReviewDto.builder()
        .id(UUID.randomUUID())
        .reviewId(reviewId2)
        .build();

    CursorPageResponsePopularReviewDto responseDto = new CursorPageResponsePopularReviewDto(
        List.of(popular1, popular2),
        "2",
        LocalDateTime.now(),
        2,
        2,
        false
    );

    when(reviewService.popular(any(RankingReviewRequest.class))).thenReturn(responseDto);

    mockMvc.perform(get("/api/reviews/popular")
            .param("period", "DAILY")
            .param("direction", "DESC")
            .param("cursor", "2025-06-01T00:00:00")
            .param("limit", "2")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].reviewId").value(reviewId1.toString()))
        .andExpect(jsonPath("$.content[1].reviewId").value(reviewId2.toString()))
        .andExpect(jsonPath("$.nextCursor").value("2"))
        .andExpect(jsonPath("$.size").value(2))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.hasNext").value(false))
        .andDo(print());

    verify(reviewService).popular(any(RankingReviewRequest.class));
  }
}
