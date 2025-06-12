package com.sprint.deokhugamteam7.domain.user.repository;


import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.comment.entity.Comment;
import com.sprint.deokhugamteam7.domain.review.entity.RankingReview;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.review.entity.ReviewLike;
import com.sprint.deokhugamteam7.domain.user.config.TestQueryDslConfig;
import com.sprint.deokhugamteam7.domain.user.dto.PowerUserSearchCondition;
import com.sprint.deokhugamteam7.domain.user.dto.UserActivity;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.entity.UserScore;
import com.sprint.deokhugamteam7.domain.user.repository.custom.UserQueryRepositoryImpl;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(TestQueryDslConfig.class)
public class UserQueryRepositoryTest {

  @Autowired
  private EntityManager em;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserScoreRepository userScoreRepository;

  @Autowired
  private UserQueryRepository userQueryRepository;

  private User user;

  private LocalDate baseDate;

  @BeforeEach
  void setUp() {
    user = userRepository.save(User.create("test@example.com", "tester", "password"));
    baseDate = LocalDate.now(ZoneId.of("Asia/Seoul"));
  }

  @Test
  @DisplayName("유저 활동 점수 집계")
  void collectUserActivityScoresTest() {
    // given
    Book book = Book.create("제목", "작성자", "출판사", baseDate).build();

    em.persist(book);

    Review review = Review.create(book, user, "리뷰", 5);
    em.persist(review);

    RankingReview rankingReview = RankingReview.create(review, 65.0, Period.DAILY);
    em.persist(rankingReview);

    ReviewLike like1 = ReviewLike.create(user, review);
    ReviewLike like2 = ReviewLike.create(user, review);
    em.persist(like1);
    em.persist(like2);

    Comment comment1 = Comment.create(user, review, "댓글 1");
    Comment comment2 = Comment.create(user, review, "댓글 2");
    em.persist(comment1);
    em.persist(comment2);

    em.flush();
    em.clear();

    // when
    List<UserActivity> activities = userQueryRepository.collectUserActivityScores(Period.DAILY, baseDate);

    // then
    assertThat(activities).isNotEmpty();
    UserActivity result = activities.get(0);

    assertThat(result.userId()).isEqualTo(user.getId());
    assertThat(result.reviewScoreSum()).isEqualTo(65.0);
    assertThat(result.likeCount()).isEqualTo(2L);
    assertThat(result.commentCount()).isEqualTo(2L);
  }

  @Test
  @DisplayName("조건에 맞는 파워 유저 점수 조회")
  void findPowerUserScoresByPeriodTest() {
    // given
    UserScore score = UserScore.create(user, Period.DAILY, 10.0, 5, 3);
    userScoreRepository.save(score);
    em.flush();
    em.clear();

    PowerUserSearchCondition condition = new PowerUserSearchCondition();
    condition.setPeriod(Period.DAILY);
    condition.setSize(10);
    condition.setDirection(org.springframework.data.domain.Sort.Direction.DESC);

    // when
    List<UserScore> results = userQueryRepository.findPowerUserScoresByPeriod(condition);

    // then
    assertThat(results).isNotEmpty();
    assertThat(results.get(0).getUser().getEmail()).isEqualTo("test@example.com");
  }

  @Test
  @DisplayName("조건에 맞는 파워 유저 개수 조회")
  void countByConditionTest() {
    // given
    UserScore score = UserScore.create(user, Period.DAILY, 10.0, 5, 3);
    userScoreRepository.save(score);
    em.flush();
    em.clear();

    PowerUserSearchCondition condition = new PowerUserSearchCondition();
    condition.setPeriod(Period.DAILY);

    // when
    Long count = userQueryRepository.countByCondition(condition);

    // then
    assertThat(count).isEqualTo(1L);
  }
}
