package com.sprint.deokhugamteam7.domain.user.repository.custom;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.comment.entity.QComment;
import com.sprint.deokhugamteam7.domain.review.entity.QRankingReview;
import com.sprint.deokhugamteam7.domain.review.entity.QReview;
import com.sprint.deokhugamteam7.domain.review.entity.QReviewLike;
import com.sprint.deokhugamteam7.domain.user.dto.PowerUserSearchCondition;
import com.sprint.deokhugamteam7.domain.user.dto.UserActivity;
import com.sprint.deokhugamteam7.domain.user.entity.QUser;
import com.sprint.deokhugamteam7.domain.user.entity.QUserScore;
import com.sprint.deokhugamteam7.domain.user.entity.UserScore;
import com.sprint.deokhugamteam7.domain.user.repository.UserQueryRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

  private static final int SIZE_OFFSET = 1;

  private final JPAQueryFactory queryFactory;

  private final QUserScore userScore = QUserScore.userScore;
  private final QUser user = QUser.user;
  private final QReview review = QReview.review;
  private final QRankingReview rankingReview = QRankingReview.rankingReview;
  private final QReviewLike reviewLike = QReviewLike.reviewLike;
  private final QComment comment = QComment.comment;

  @Override
  public List<UserScore> findPowerUserScoresByPeriod(PowerUserSearchCondition condition) {
    Double cursorScore = condition.parsedCursorScore();
    LocalDateTime afterCreatedAt = condition.getAfter();

    BooleanBuilder whereCondition = new BooleanBuilder();
    whereCondition.and(userScore.period.eq(condition.getPeriod()));

    if (cursorScore != null && afterCreatedAt != null) {
      BooleanBuilder cursorCondition = new BooleanBuilder();
      if (condition.getDirection() == Sort.Direction.DESC) {
        cursorCondition.and(userScore.score.lt(cursorScore)
            .or(userScore.score.eq(cursorScore).and(userScore.createdAt.lt(afterCreatedAt))));
      } else {
        cursorCondition.and(userScore.score.gt(cursorScore)
            .or(userScore.score.eq(cursorScore).and(userScore.createdAt.gt(afterCreatedAt))));
      }
      whereCondition.and(cursorCondition);
    }

    JPAQuery<UserScore> query = queryFactory
        .selectFrom(userScore)
        .join(userScore.user, QUser.user).fetchJoin()
        .where(whereCondition);

    if (condition.getDirection() == Sort.Direction.DESC) {
      query.orderBy(userScore.score.desc(), userScore.createdAt.desc());
    } else {
      query.orderBy(userScore.score.asc(), userScore.createdAt.asc());
    }

    return query.limit(condition.getSize() + SIZE_OFFSET).fetch();
  }


  @Override
  public List<UserActivity> collectUserActivityScores(Period period, LocalDate baseDate) {
    LocalDateTime start = getStartDate(period, baseDate).atStartOfDay();
    LocalDateTime end = baseDate.plusDays(1).atStartOfDay(); // exclusive

    // 유저별 활동 데이터 수집
    Map<UUID, Double> reviewScoreMap = queryFactory
        .select(rankingReview.review.user.id, rankingReview.score.sum())
        .from(rankingReview)
        .where(rankingReview.period.eq(period),
            rankingReview.reviewCreatedAt.between(start, end.minusNanos(1)))
        .groupBy(rankingReview.review.user.id)
        .fetch()
        .stream()
        .collect(Collectors.toMap(
            tuple -> tuple.get(0, UUID.class),
            tuple -> Optional.ofNullable(tuple.get(1, Double.class)).orElse(0.0)
        ));

    Map<UUID, Long> likeCountMap = queryFactory
        .select(review.user.id, reviewLike.count())
        .from(reviewLike)
        .join(reviewLike.review, review)
        .where(reviewLike.createdAt.between(start, end.minusNanos(1)))
        .groupBy(review.user.id)
        .fetch()
        .stream()
        .collect(Collectors.toMap(
            tuple -> tuple.get(0, UUID.class),
            tuple -> tuple.get(1, Long.class)
        ));

    Map<UUID, Long> commentCountMap = queryFactory
        .select(review.user.id, comment.count())
        .from(comment)
        .join(comment.review, review)
        .where(comment.createdAt.between(start, end.minusNanos(1)))
        .groupBy(review.user.id)
        .fetch()
        .stream()
        .collect(Collectors.toMap(
            tuple -> tuple.get(0, UUID.class),
            tuple -> tuple.get(1, Long.class)
        ));

    // 유저 ID 기준으로 모든 항목 병합
    Set<UUID> userIds = new HashSet<>();
    userIds.addAll(reviewScoreMap.keySet());
    userIds.addAll(likeCountMap.keySet());
    userIds.addAll(commentCountMap.keySet());

    return userIds.stream()
        .map(userId -> new UserActivity(
            userId,
            reviewScoreMap.getOrDefault(userId, 0.0),
            likeCountMap.getOrDefault(userId, 0L),
            commentCountMap.getOrDefault(userId, 0L)
        ))
        .toList();
  }

  private LocalDate getStartDate(Period period, LocalDate baseDate) {
    return switch (period) {
      case DAILY -> baseDate;
      case WEEKLY -> baseDate.minusDays(6);
      case MONTHLY -> baseDate.withDayOfMonth(1);
      case ALL_TIME -> LocalDate.of(2000, 1, 1); // 임의의 과거 시점
    };
  }

  @Override
  public Long countByCondition(PowerUserSearchCondition condition) {

    BooleanBuilder builder = new BooleanBuilder();
    builder.and(userScore.period.eq(condition.getPeriod()));

    return queryFactory
        .select(userScore.count())
        .from(userScore)
        .where(builder)
        .fetchOne();
  }
}
