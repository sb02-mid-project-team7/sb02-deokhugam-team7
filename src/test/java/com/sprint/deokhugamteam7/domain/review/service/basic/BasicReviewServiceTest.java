package com.sprint.deokhugamteam7.domain.review.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.repository.BookRepository;
import com.sprint.deokhugamteam7.domain.comment.repository.CommentRepository;
import com.sprint.deokhugamteam7.domain.notification.repository.NotificationRepository;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewCreateRequest;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewSearchCondition;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewUpdateRequest;
import com.sprint.deokhugamteam7.domain.review.dto.response.CursorPageResponseReviewDto;
import com.sprint.deokhugamteam7.domain.review.dto.response.ReviewDto;
import com.sprint.deokhugamteam7.domain.review.dto.response.ReviewLikeDto;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.review.entity.ReviewLike;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewLikeRepository;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewRepository;
import com.sprint.deokhugamteam7.domain.review.repository.custom.ReviewRepositoryCustom;
import com.sprint.deokhugamteam7.domain.review.service.BasicReviewService;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import com.sprint.deokhugamteam7.exception.review.ReviewException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

  @Mock
  private ReviewRepositoryCustom reviewRepositoryCustom;

  @Mock
  private NotificationRepository notificationRepository;

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
  @DisplayName("리뷰 생성 - 성공")
  void createReview_Success() {
    when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(user));
    when(bookRepository.findByIdAndIsDeletedFalse(bookId)).thenReturn(Optional.of(book));
    when(reviewRepository.existsByUserAndBookAndIsDeletedIsFalse(any(User.class),
        any(Book.class))).thenReturn(false);
    ReviewCreateRequest request
        = new ReviewCreateRequest(bookId, userId, "책의 리뷰입니다.", 3);

    ReviewDto result = reviewService.create(request);

    assertThat(result).isNotNull();
    assertThat(result.content()).isEqualTo("책의 리뷰입니다.");
    assertThat(result.rating()).isEqualTo(3);
  }

  @Test
  @DisplayName("리뷰 생성 - 실패")
  void createReview_Fail() {
    when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(user));
    when(bookRepository.findByIdAndIsDeletedFalse(bookId)).thenReturn(Optional.of(book));
    when(reviewRepository.existsByUserAndBookAndIsDeletedIsFalse(any(User.class),
        any(Book.class))).thenReturn(true);

    ReviewCreateRequest request
        = new ReviewCreateRequest(bookId, userId, "책의 리뷰입니다.", 3);

    assertThrows(ReviewException.class, () -> reviewService.create(request));
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

  @Test
  @DisplayName("리뷰 삭제 - Soft")
  void updateReview_Soft() {
    // given
    UUID reviewId = UUID.randomUUID();
    Review review = Review.create(book, user, "책의 리뷰입니다.", 3);
    ReflectionTestUtils.setField(review, "id", reviewId);
    book.setReviews(new ArrayList<>(List.of(review)));

    // when
    when(reviewRepository.findByIdAndIsDeletedIsFalse(reviewId)).thenReturn(Optional.of(review));

    reviewService.deleteSoft(reviewId, userId);

    // then
    assertThat(review.getIsDeleted()).isTrue();
  }

  @Test
  @DisplayName("리뷰 삭제 - Hard")
  void updateReview_Hard() {
    // given
    UUID reviewId = UUID.randomUUID();
    Review review = Review.create(book, user, "책의 리뷰입니다.", 3);
    ReflectionTestUtils.setField(review, "id", reviewId);

    // when
    when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

    reviewService.deleteHard(reviewId, userId);

    // then
    verify(reviewRepository).delete(review);
  }

  @Test
  @DisplayName("리뷰 좋아요 - 추가 성공")
  void addLikeReview_Success() {
    // given
    UUID reviewId = UUID.randomUUID();
    Review review = Review.create(book, user, "책의 리뷰입니다.", 3);
    ReflectionTestUtils.setField(review, "id", reviewId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
    when(reviewLikeRepository.findByReviewIdAndUserId(reviewId, userId))
        .thenReturn(Optional.empty());

    // when
    ReviewLikeDto result = reviewService.like(reviewId, userId);

    // then
    assertThat(result.reviewId()).isEqualTo(reviewId);
    assertThat(result.userId()).isEqualTo(userId);
    assertThat(result.liked()).isTrue();

    verify(reviewLikeRepository).save(any(ReviewLike.class));
    verify(reviewLikeRepository, never()).delete(any());
  }

  @Test
  @DisplayName("리뷰 좋아요 - 취소 성공")
  void removeLikeReview_Success() {
    // given
    UUID reviewId = UUID.randomUUID();
    Review review = Review.create(book, user, "책의 리뷰입니다.", 3);
    ReflectionTestUtils.setField(review, "id", reviewId);

    ReviewLike reviewLike = ReviewLike.create(user, review);
    ReflectionTestUtils.setField(reviewLike, "id", UUID.randomUUID());

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
    when(reviewLikeRepository.findByReviewIdAndUserId(reviewId, userId))
        .thenReturn(Optional.of(reviewLike));

    // when
    ReviewLikeDto result = reviewService.like(reviewId, userId);

    assertThat(result.reviewId()).isEqualTo(reviewId);
    assertThat(result.userId()).isEqualTo(userId);
    assertThat(result.liked()).isFalse();

    verify(reviewLikeRepository, never()).save(any(ReviewLike.class));
    verify(reviewLikeRepository).delete(any());
  }

  @Test
  @DisplayName("리뷰 상세 조회")
  void findById_returnsReviewDto() {
    UUID reviewId = UUID.randomUUID();
    Review review = Review.create(book, user, "내용입니다.", 5);
    ReflectionTestUtils.setField(review, "id", reviewId);

    when(userRepository.existsById(userId)).thenReturn(true);
    when(reviewRepository.findByIdWithUserAndBook(reviewId)).thenReturn(Optional.of(review));
    when(reviewLikeRepository.countByReviewId(reviewId)).thenReturn(3);
    when(commentRepository.countByReviewIdAndIsDeletedFalse(reviewId)).thenReturn(2);
    when(reviewLikeRepository.existsByUserIdAndReviewId(userId, reviewId)).thenReturn(true);

    ReviewDto result = reviewService.findById(reviewId, userId);

    verify(userRepository).existsById(userId);
    verify(reviewRepository).findByIdWithUserAndBook(reviewId);
    verify(reviewLikeRepository).countByReviewId(reviewId);
    verify(commentRepository).countByReviewIdAndIsDeletedFalse(reviewId);
    verify(reviewLikeRepository).existsByUserIdAndReviewId(userId, reviewId);

    assertThat(result.id()).isEqualTo(reviewId);
    assertThat(result.userId()).isEqualTo(userId);
    assertThat(result.bookId()).isEqualTo(bookId);
    assertThat(result.content()).isEqualTo("내용입니다.");
    assertThat(result.rating()).isEqualTo(5);
    assertThat(result.likeCount()).isEqualTo(3);
    assertThat(result.commentCount()).isEqualTo(2);
    assertThat(result.likedByMe()).isTrue();
  }

  @Test
  @DisplayName("리뷰 목록 조회")
  void findAll_returnsCursorPageResponseReviewDto() {
    // given
    ReviewSearchCondition condition = new ReviewSearchCondition();
    condition.setLimit(2);

    UUID user2Id = UUID.randomUUID();
    User user2 = User.create("test2@gmail.com", "test2", "test1234!");
    ReflectionTestUtils.setField(user2, "id", user2Id);

    UUID user3Id = UUID.randomUUID();
    User user3 = User.create("test3@gmail.com", "test3", "test1234!");
    ReflectionTestUtils.setField(user3, "id", user3Id);

    Review review1 = Review.create(book, user, "리뷰1", 5);
    Review review2 = Review.create(book, user2, "리뷰2", 4);
    Review review3 = Review.create(book, user3, "리뷰3", 3);

    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    UUID id3 = UUID.randomUUID();

    LocalDateTime created1 = LocalDateTime.of(2025, 5, 10, 12, 0);
    LocalDateTime created2 = LocalDateTime.of(2025, 5, 9, 12, 0);
    LocalDateTime created3 = LocalDateTime.of(2025, 5, 8, 12, 0);

    ReflectionTestUtils.setField(review1, "id", id1);
    ReflectionTestUtils.setField(review2, "id", id2);
    ReflectionTestUtils.setField(review3, "id", id3);

    ReflectionTestUtils.setField(review1, "createdAt", created1);
    ReflectionTestUtils.setField(review2, "createdAt", created2);
    ReflectionTestUtils.setField(review3, "createdAt", created3);

    List<Review> reviews = List.of(review1, review2, review3);

    Mockito.when(reviewRepositoryCustom.findAll(condition, 2)).thenReturn(reviews);
    Mockito.when(reviewLikeRepository.countByReviewId(Mockito.any())).thenReturn(3);
    Mockito.when(commentRepository.countByReviewIdAndIsDeletedFalse(Mockito.any())).thenReturn(2);
    Mockito.when(reviewLikeRepository.existsByUserIdAndReviewId(Mockito.any(), Mockito.any()))
        .thenReturn(true);
    Mockito.when(reviewRepositoryCustom.countByCondition(condition)).thenReturn(3L);

    // when
    CursorPageResponseReviewDto result = reviewService.findAll(condition, userId);

    // then
    verify(reviewRepositoryCustom).findAll(condition, 2);
    verify(reviewLikeRepository).countByReviewId(id1);
    verify(reviewLikeRepository).countByReviewId(id2);
    verify(commentRepository).countByReviewIdAndIsDeletedFalse(id1);
    verify(commentRepository).countByReviewIdAndIsDeletedFalse(id2);
    verify(reviewLikeRepository).existsByUserIdAndReviewId(userId, id1);
    verify(reviewLikeRepository).existsByUserIdAndReviewId(userId, id2);
    verify(reviewRepositoryCustom).countByCondition(condition);

    assertThat(result.content()).hasSize(2);
    assertThat(result.hasNext()).isTrue();
    assertThat(result.totalElements()).isEqualTo(3L);
    assertThat(result.nextAfter()).isEqualTo(created2);
    assertThat(result.nextCursor()).isEqualTo(created2.toString());

    assertThat(result.content().get(0).content()).isEqualTo("리뷰1");
    assertThat(result.content().get(1).content()).isEqualTo("리뷰2");
    assertThat(result.content().get(0).likeCount()).isEqualTo(3);
    assertThat(result.content().get(0).commentCount()).isEqualTo(2);
    assertThat(result.content().get(0).likedByMe()).isTrue();
  }
}
