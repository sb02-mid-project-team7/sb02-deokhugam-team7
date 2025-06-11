package com.sprint.deokhugamteam7.domain.review.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.notification.config.TestConfig;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.review.entity.ReviewLike;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(TestConfig.class)
@DataJpaTest
@ActiveProfiles("test")
public class ReviewAndReviewLikeRepositoryTest {

  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private ReviewLikeRepository reviewLikeRepository;

  @Autowired
  private EntityManager em;

  private User user;
  private Book book;
  private Review review;

  @BeforeEach
  void setUp() {
    user = User.create("test@gmail.com", "test1", "test1234!");
    book = Book.create("test", "test", "test",
        LocalDate.of(2020, 1, 15)).build();

    review = Review.create(book, user, "리뷰입니다.", 3);

    em.persist(user);
    em.persist(book);
    em.persist(review);
    em.flush();
    em.clear();
  }

  // ReviewRepository

  @Test
  @DisplayName("리뷰 조회 시 User와 Book을 Fetch Join으로 함께 조회")
  void findByIdWithUserAndBook_shouldFetchUserAndBook() {
    Optional<Review> res = reviewRepository.findByIdWithUserAndBook(review.getId());

    assertThat(res).isPresent();
    assertThat(res.get().getUser().getId()).isEqualTo(user.getId());
    assertThat(res.get().getBook().getId()).isEqualTo(book.getId());
    assertThat(res.get().getIsDeleted()).isFalse();
  }

  // ReviewLikeRepository

  @Test
  @DisplayName("리뷰의 좋아요 수 조회")
  void countByReviewId_shouldReturnCount() {
    User user2 = User.create("test2@gmail.com", "test2", "test1234!");
    em.persist(user2);

    ReviewLike like1 = ReviewLike.create(user, review);
    ReviewLike like2 = ReviewLike.create(user2, review);
    em.persist(like1);
    em.persist(like2);
    em.flush();

    int res = reviewLikeRepository.countByReviewId(review.getId());

    assertThat(res).isEqualTo(2);
  }

  @Test
  @DisplayName("유저가 특정 리뷰에 좋아요를 누른 경우 true를 반환")
  void existsByUserIdAndReviewId_ShouldReturnTrue() {
    ReviewLike like1 = ReviewLike.create(user, review);
    em.persist(like1);
    em.flush();

    boolean res = reviewLikeRepository.existsByUserIdAndReviewId(user.getId(), review.getId());

    assertThat(res).isTrue();
  }

  @Test
  @DisplayName("주어진 reviewIds 중 유저가 좋아요를 누른 ReviewId들을 Set으로 반환")
  void findReviewIdsLikedByUser_shouldReturnOnlyLikedReviewIds() {
    User user2 = User.create("test2@gmail.com", "test2", "test1234!");
    em.persist(user2);

    Review review2 = Review.create(book, user, "두 번째 리뷰입니다.", 4);
    em.persist(review2);

    ReviewLike like1 = ReviewLike.create(user, review);
    ReviewLike like2 = ReviewLike.create(user, review2);
    ReviewLike like3 = ReviewLike.create(user2, review2);
    em.persist(like1);
    em.persist(like2);
    em.persist(like3);
    em.flush();
    em.clear();

    List<UUID> reviewIds = List.of(review.getId(), review2.getId());
    Set<UUID> likedByUser = reviewLikeRepository.findReviewIdsLikedByUser(user.getId(), reviewIds);

    assertThat(likedByUser)
        .hasSize(2)
        .containsExactlyInAnyOrder(review.getId(), review2.getId());
  }
}
