package com.sprint.deokhugamteam7.domain.review.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.repository.BookRepository;
import com.sprint.deokhugamteam7.domain.comment.repository.CommentRepository;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewCreateRequest;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewUpdateRequest;
import com.sprint.deokhugamteam7.domain.review.dto.response.ReviewDto;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewLikeRepository;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewRepository;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class BasicReviewServiceTest {

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private BookRepository bookRepository;

  @Mock
  private ReviewLikeRepository reviewLikeRepository;

  @Mock
  private CommentRepository commentRepository;

  @InjectMocks
  private BasicReviewService reviewService;

  private UUID bookId;
  private UUID userId;

  private User user;
  private Book book;

  @BeforeEach
  void setUp() {
    bookId = UUID.randomUUID();
    userId = UUID.randomUUID();

    user = User.create("test@gmail.com", "test", "test1234!");
    book = Book.create(
        "도메인 주도 설계", "에릭 에반스", "한빛미디어",
        LocalDate.of(2020, 1, 15)
    ).build();

    ReflectionTestUtils.setField(user, "id", userId);
    ReflectionTestUtils.setField(book, "id", bookId);
  }

  @Test
  @DisplayName("리뷰 생성")
  void createReview() {
    // given
    when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(user));
    when(bookRepository.findByIdAndIsDeletedFalse(bookId)).thenReturn(Optional.of(book));
    ReviewCreateRequest request
        = new ReviewCreateRequest(bookId, userId, "책의 리뷰입니다.", 3);

    // when
    ReviewDto result = reviewService.create(request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.content()).isEqualTo("책의 리뷰입니다.");
    assertThat(result.rating()).isEqualTo(3);
  }

  @Test
  @DisplayName("리뷰 수정 - 성공")
  void updateReview_Success() {
    // given
    UUID reviewId = UUID.randomUUID();
    Review review = Review.create(book, user, "책의 리뷰입니다.", 3);
    ReflectionTestUtils.setField(review, "id", reviewId);
    ReviewUpdateRequest request
        = new ReviewUpdateRequest("리뷰 변경합니다.", 5);

    when(userRepository.existsById(userId)).thenReturn(true);
    when(reviewRepository.findByIdWithUserAndBook(reviewId)).thenReturn(Optional.of(review));
    when(reviewLikeRepository.countByReviewId(reviewId)).thenReturn(2);
    when(commentRepository.countByReviewIdAndIsDeletedFalse(reviewId)).thenReturn(4);
    when(reviewLikeRepository.existsByUserIdAndReviewId(userId, reviewId)).thenReturn(true);

    // when
    ReviewDto result = reviewService.update(reviewId, userId, request);

    // then
    ReviewDto expectedDto = ReviewDto.of(review, 2, 4, true);

    assertThat(expectedDto.id()).isEqualTo(result.id());
    assertThat(expectedDto.content()).isEqualTo(result.content());
    assertThat(expectedDto.rating()).isEqualTo(result.rating());
    assertThat(expectedDto.likeCount()).isEqualTo(result.likeCount());
    assertThat(expectedDto.commentCount()).isEqualTo(result.commentCount());
    assertThat(expectedDto.likedByMe()).isEqualTo(result.likedByMe());
  }
}
