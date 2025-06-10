package com.sprint.deokhugamteam7.domain.review.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.comment.entity.Comment;
import com.sprint.deokhugamteam7.domain.notification.config.TestConfig;
import com.sprint.deokhugamteam7.domain.review.dto.request.RankingReviewRequest;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewSearchCondition;
import com.sprint.deokhugamteam7.domain.review.entity.RankingReview;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.review.entity.ReviewLike;
import com.sprint.deokhugamteam7.domain.review.repository.custom.ReviewRepositoryCustom;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@Import(TestConfig.class)
@DataJpaTest
@ActiveProfiles("test")
public class ReviewRepositoryImplTest {

  @Autowired
  private ReviewRepositoryCustom reviewRepositoryCustom;

  @Autowired
  private EntityManager em;

  private User user;
  private User user2;
  private User user3;
  private User user4;
  private Book book;
  private Book book2;
  private Review review;
  private Review review2;
  private Review review3;
  private Review review4;

  private final int rating = 3;

  private final LocalDateTime day = LocalDateTime.of(2025, 6, 4, 3, 0);
  private final LocalDateTime week = LocalDateTime.of(2025, 5, 30, 3, 0);
  private final LocalDateTime month = LocalDateTime.of(2025, 5, 10, 3, 0);
  private final LocalDateTime allTime = LocalDateTime.of(2024, 6, 4, 4, 0);
  private final LocalDateTime end = LocalDate.of(2025, 6, 5).atStartOfDay();

  @BeforeEach
  void setUp() {
    user = User.create("test@gmail.com", "test1", "test1234!");
    book = Book.create("테스트", "test", "test",
        LocalDate.of(2020, 1, 15)).build();
    review = Review.create(book, user, "리뷰1", rating);

    em.persist(user);
    em.persist(book);
    em.persist(review);
    em.flush();
  }

  @Test
  @DisplayName("검색 조건과 정렬 조건, 제한에 맞는 리뷰를 조회")
  void findAll_ShouldReturnReviewList() {
    ReviewSearchCondition condition = createSearchCondition();

    createOtherReviews();

    List<Review> res = reviewRepositoryCustom.findAll(condition, condition.getLimit());
    // - DESC 조회, 키워드는 유저의 닉네임 or 책의 제목 or 리뷰의 내용 중 하나라도 일치하면 포함됨.
    // - isDeleted가 false 이어야 함. limit + 1개의 List가 반환되어야 함.

    assertThat(res).hasSize(2);
    assertThat(res).hasSizeLessThanOrEqualTo(condition.getLimit() + 1);
    assertThat(res.get(0).getId()).isEqualTo(review2.getId());
    assertThat(res.get(0).getContent()).isEqualTo("리뷰2");
    assertThat(res.get(1).getContent()).isEqualTo("리뷰1");
    assertThat(res).noneMatch(review -> review.getId().equals(review3.getId()));
    assertThat(res).noneMatch(review -> review.getId().equals(review4.getId()));
  }

  @Test
  @DisplayName("커서 기반 rating 정렬 리뷰 조회 (DESC)")
  void findAll_withCursorAndRatingOrder_ShouldReturnPagedList() {
    // given
    createOtherReviews();

    ReviewSearchCondition condition = new ReviewSearchCondition();
    condition.setOrderBy("rating");
    condition.setDirection("DESC");
    condition.setLimit(2);
    condition.setCursor("3");
    condition.setAfter(review2.getCreatedAt());

    // when
    List<Review> result = reviewRepositoryCustom.findAll(condition, condition.getLimit());

    // then
    assertThat(result).allSatisfy(review -> {
      assertThat(review.getRating()).isLessThanOrEqualTo(3);
      if (review.getRating() == 3) {
        assertThat(review.getCreatedAt()).isBeforeOrEqualTo(review2.getCreatedAt());
      }
    });
  }

  @Test
  @DisplayName("검색 조건과 정렬 조건, 제한에 맞는 모든 리뷰의 수 반환")
  void countByCondition_ShouldReturnReviewCount() {
    ReviewSearchCondition condition = createSearchCondition();
    createOtherReviews();

    long res = reviewRepositoryCustom.countByCondition(condition);

    assertThat(res).isEqualTo(2);
  }

  @Test
  @DisplayName("리뷰별 좋아요 수를 기간별로 집계")
  void findLikeCountsByPeriod_ShouldReturnCountMapWithinPeriod() {
    createOtherReviews();
    ReflectionTestUtils.setField(review4, "isDeleted", false);

    ReviewLike like1 = ReviewLike.create(user, review);
    ReviewLike like2 = ReviewLike.create(user, review2);
    ReviewLike like3 = ReviewLike.create(user2, review2);
    ReviewLike like4 = ReviewLike.create(user, review3);
    ReviewLike like5 = ReviewLike.create(user, review4);
    ReviewLike like6 = ReviewLike.create(user2, review4);
    em.persist(like1);
    em.persist(like2);
    em.persist(like3);
    em.persist(like4);
    em.persist(like5);
    em.persist(like6);

    updateReviewLikeCreatedAt(day, like1.getId());
    updateReviewLikeCreatedAt(week, like2.getId());
    updateReviewLikeCreatedAt(week, like3.getId());
    updateReviewLikeCreatedAt(month, like4.getId());
    updateReviewLikeCreatedAt(allTime, like5.getId());
    updateReviewLikeCreatedAt(allTime, like6.getId());
    em.flush();
    em.clear();

    Map<UUID, Long> dayRes = reviewRepositoryCustom.findLikeCountsByPeriod(end.minusDays(1), end);
    Map<UUID, Long> weekRes = reviewRepositoryCustom.findLikeCountsByPeriod(end.minusWeeks(1), end);
    Map<UUID, Long> monthRes = reviewRepositoryCustom.findLikeCountsByPeriod(end.minusMonths(1),
        end);
    Map<UUID, Long> allRes = reviewRepositoryCustom.findLikeCountsByPeriod(null, null);

    assertThat(dayRes).hasSize(1);
    assertThat(dayRes.get(review.getId())).isEqualTo(1L);
    assertThat(weekRes).hasSize(2);
    assertThat(weekRes.get(review2.getId())).isEqualTo(2L);
    assertThat(monthRes).hasSize(3);
    assertThat(monthRes.get(review3.getId())).isEqualTo(1L);
    assertThat(allRes).hasSize(4);
    assertThat(allRes.get(review4.getId())).isEqualTo(2L);
  }

  ReviewSearchCondition createSearchCondition() {
    ReviewSearchCondition condition = new ReviewSearchCondition();
    condition.setUserId(null);
    condition.setBookId(null);
    condition.setKeyword("테스트");
    condition.setOrderBy("createdAt");
    condition.setDirection("DESC");
    condition.setLimit(1);
    condition.setRequestUserId(user.getId());

    return condition;
  }

  @Test
  @DisplayName("리뷰별 댓글 수를 기간별로 집계")
  void findCommentCountsByPeriod_ShouldReturnCountMapWithinPeriod() {
    createOtherReviews();
    ReflectionTestUtils.setField(review4, "isDeleted", false);

    Comment c1 = Comment.create(user, review, "<UNK>1");
    Comment c2 = Comment.create(user, review2, "<UNK>2");
    Comment c3 = Comment.create(user2, review2, "<UNK>3");
    Comment c4 = Comment.create(user, review3, "<UNK>4");
    Comment c5 = Comment.create(user, review4, "<UNK>5");
    Comment c6 = Comment.create(user2, review4, "<UNK>6");
    em.persist(c1);
    em.persist(c2);
    em.persist(c3);
    em.persist(c4);
    em.persist(c5);
    em.persist(c6);

    updateCommentCreatedAt(day, c1.getId());
    updateCommentCreatedAt(week, c2.getId());
    updateCommentCreatedAt(week, c3.getId());
    updateCommentCreatedAt(month, c4.getId());
    updateCommentCreatedAt(allTime, c5.getId());
    updateCommentCreatedAt(allTime, c6.getId());
    em.flush();
    em.clear();

    Map<UUID, Long> dayRes = reviewRepositoryCustom.findCommentCountsByPeriod(end.minusDays(1),
        end);
    Map<UUID, Long> weekRes = reviewRepositoryCustom.findCommentCountsByPeriod(end.minusWeeks(1),
        end);
    Map<UUID, Long> monthRes = reviewRepositoryCustom.findCommentCountsByPeriod(end.minusMonths(1),
        end);
    Map<UUID, Long> allRes = reviewRepositoryCustom.findCommentCountsByPeriod(null, null);

    assertThat(dayRes).hasSize(1);
    assertThat(dayRes.get(review.getId())).isEqualTo(1L);
    assertThat(weekRes).hasSize(2);
    assertThat(weekRes.get(review2.getId())).isEqualTo(2L);
    assertThat(monthRes).hasSize(3);
    assertThat(monthRes.get(review3.getId())).isEqualTo(1L);
    assertThat(allRes).hasSize(4);
    assertThat(allRes.get(review4.getId())).isEqualTo(2L);
  }

  @Test
  @DisplayName("해당 기간의 랭킹리뷰 조회")
  void findRankingReviewsByPeriod_ShouldReturnRankingReviewWithinPeriod() {
    createOtherReviews();

    RankingReview ranking1 = RankingReview.create(review, 9.0, Period.DAILY);
    RankingReview ranking2 = RankingReview.create(review2, 7.5, Period.DAILY);
    RankingReview ranking3 = RankingReview.create(review3, 8.2, Period.DAILY);
    RankingReview ranking4 = RankingReview.create(review4, 10.0, Period.WEEKLY);

    em.persist(ranking1);
    em.persist(ranking2);
    em.persist(ranking3);
    em.persist(ranking4);
    em.flush();
    em.clear();

    RankingReviewRequest request = new RankingReviewRequest();
    request.setLimit(2);

    List<RankingReview> res = reviewRepositoryCustom.findRankingReviewsByPeriod(request, 1);

    assertThat(res).hasSize(2);
    assertThat(res.get(0).getScore()).isEqualTo(9.0);
    assertThat(res.get(1).getScore()).isEqualTo(8.2);

    // fetchJoin 검증
    for (RankingReview rr : res) {
      assertThat(rr.getReview().getUser().getEmail()).isNotNull();
      assertThat(rr.getReview().getBook().getTitle()).isNotNull();
    }
  }

  @Test
  @DisplayName("해당 기간의 모든 랭킹리뷰 조회")
  void countRankingReviewByPeriod_ShouldReturnCountWithinPeriod() {
    createOtherReviews();

    RankingReview ranking1 = RankingReview.create(review, 9.0, Period.DAILY);
    RankingReview ranking2 = RankingReview.create(review2, 7.5, Period.DAILY);
    RankingReview ranking3 = RankingReview.create(review3, 8.2, Period.DAILY);
    RankingReview ranking4 = RankingReview.create(review4, 10.0, Period.WEEKLY);

    em.persist(ranking1);
    em.persist(ranking2);
    em.persist(ranking3);
    em.persist(ranking4);
    em.flush();
    em.clear();

    long res = reviewRepositoryCustom.countRakingReviewByPeriod(Period.DAILY);

    assertThat(res).isEqualTo(3);
  }

  @Test
  @DisplayName("여러 reviewId에 대해 각각의 좋아요 수를 Map으로 반환")
  void countLikesByReviewIds_ShouldReturnCounts() {
    createOtherReviews();

    ReviewLike like1 = ReviewLike.create(user, review);
    ReviewLike like2 = ReviewLike.create(user2, review2);
    ReviewLike like3 = ReviewLike.create(user3, review2);
    ReviewLike like4 = ReviewLike.create(user4, review3);
    em.persist(like1);
    em.persist(like2);
    em.persist(like3);
    em.persist(like4);
    em.flush();
    em.clear();

    List<UUID> reviewIds = List.of(review.getId(), review2.getId(), review3.getId());
    Map<UUID, Integer> result = reviewRepositoryCustom.countLikesByReviewIds(reviewIds);

    assertThat(result).hasSize(3);
    assertThat(result.get(review.getId())).isEqualTo(1);
    assertThat(result.get(review2.getId())).isEqualTo(2);
    assertThat(result.get(review3.getId())).isEqualTo(1);
  }

  @Test
  @DisplayName("여러 reviewId에 대해 각각의 삭제되지 않은 댓글 수를 Map으로 반환")
  void countCommentsByReviewIds_ShouldReturnCounts() {
    createOtherReviews();

    Comment comment1 = Comment.create(user, review, "리뷰1의 댓글");
    Comment comment2 = Comment.create(user2, review2, "리뷰2의 댓글1");
    Comment comment3 = Comment.create(user3, review2, "리뷰2의 댓글2");
    comment3.delete();
    Comment comment4 = Comment.create(user4, review3, "리뷰3의 댓글");

    em.persist(comment1);
    em.persist(comment2);
    em.persist(comment3);
    em.persist(comment4);
    em.flush();
    em.clear();

    List<UUID> reviewIds = List.of(review.getId(), review2.getId(), review3.getId());
    Map<UUID, Integer> result = reviewRepositoryCustom.countCommentsByReviewIds(reviewIds);

    assertThat(result).hasSize(3);
    assertThat(result.get(review.getId())).isEqualTo(1);
    assertThat(result.get(review2.getId())).isEqualTo(1);
    assertThat(result.get(review3.getId())).isEqualTo(1);
  }

  void createOtherReviews() {
    user2 = User.create("test2@gmail.com", "test2", "test1234!");
    user3 = User.create("test3@gmail.com", "test3", "test1234!");
    user4 = User.create("test4@gmail.com", "test4", "test1234!");
    book2 = Book.create("book", "book", "book", LocalDate.of(2020, 1, 15)).build();
    em.persist(user2);
    em.persist(user3);
    em.persist(user4);
    em.persist(book2);

    review2 = Review.create(book, user2, "리뷰2", rating);
    review3 = Review.create(book2, user3, "리뷰3", rating);
    review4 = Review.create(book, user4, "리뷰 테스트4", rating);
    review4.delete();

    em.persist(review2);
    em.persist(review3);
    em.persist(review4);
    em.flush();
  }

  void updateReviewLikeCreatedAt(LocalDateTime updatedAt, UUID reviewLikeId) {
    em.createQuery("UPDATE ReviewLike rl SET rl.createdAt = :createdAt WHERE rl.id = :id")
        .setParameter("createdAt", updatedAt)
        .setParameter("id", reviewLikeId)
        .executeUpdate();
  }

  void updateCommentCreatedAt(LocalDateTime updatedAt, UUID commentId) {
    em.createQuery("UPDATE Comment c SET c.createdAt = :createdAt WHERE c.id = :id")
        .setParameter("createdAt", updatedAt)
        .setParameter("id", commentId)
        .executeUpdate();
  }
}
