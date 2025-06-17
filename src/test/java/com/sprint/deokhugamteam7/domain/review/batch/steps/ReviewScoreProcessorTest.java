package com.sprint.deokhugamteam7.domain.review.batch.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.review.batch.step.ReviewScoreProcessor;
import com.sprint.deokhugamteam7.domain.review.dto.ReviewActivity;
import com.sprint.deokhugamteam7.domain.review.entity.RankingReview;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewRepository;
import com.sprint.deokhugamteam7.exception.review.ReviewException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewScoreProcessorTest {

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private Review mockReview;  // Mockito mock 객체

  private ReviewScoreProcessor processor;
  private UUID reviewId;

  @BeforeEach
  void setUp() {
    // setUp 에서는 오직 객체 생성과 ID 초기화만!
    processor = new ReviewScoreProcessor(reviewRepository, "daily");
    reviewId = UUID.randomUUID();
  }

  @Test
  @DisplayName("좋아요와 댓글 개수로 score를 계산하여 RankingReview를 반환한다")
  void process_calculatesScoreCorrectly() {
    // given: 테스트 메서드 내부에서만 stub 호출
    when(reviewRepository.findById(reviewId))
        .thenReturn(Optional.of(mockReview));

    ReviewActivity activity = new ReviewActivity(reviewId, 3L, 5L);

    // when
    RankingReview result = processor.process(activity);

    // then
    assertThat(result.getReview()).isSameAs(mockReview);
    assertThat(result.getPeriod()).isEqualTo(Period.DAILY);
    assertThat(result.getScore()).isEqualTo(4.4); // 3*0.3 + 5*0.7 = 4.4
  }

  @Test
  @DisplayName("존재하지 않는 reviewId 입력 시 ReviewException을 던진다")
  void process_whenReviewNotFound_throwsException() {
    // given: 이 stub 도 역시 메서드 내부로 이동
    when(reviewRepository.findById(reviewId))
        .thenReturn(Optional.empty());

    ReviewActivity activity = new ReviewActivity(reviewId, 1L, 1L);

    // when / then
    assertThrows(ReviewException.class, () -> processor.process(activity));
  }
}

