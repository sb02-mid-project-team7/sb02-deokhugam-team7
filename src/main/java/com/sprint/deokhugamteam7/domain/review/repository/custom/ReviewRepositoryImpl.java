package com.sprint.deokhugamteam7.domain.review.repository.custom;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.entity.QBook;
import com.sprint.deokhugamteam7.domain.comment.entity.QComment;
import com.sprint.deokhugamteam7.domain.review.dto.ReviewCountDto;
import com.sprint.deokhugamteam7.domain.review.dto.request.RankingReviewRequest;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewSearchCondition;
import com.sprint.deokhugamteam7.domain.review.entity.QRankingReview;
import com.sprint.deokhugamteam7.domain.review.entity.QReview;
import com.sprint.deokhugamteam7.domain.review.entity.QReviewLike;
import com.sprint.deokhugamteam7.domain.review.entity.RankingReview;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.user.entity.QUser;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  QReview review = QReview.review;
  QUser user = QUser.user;
  QBook book = QBook.book;
  QReviewLike rl = QReviewLike.reviewLike;
  QComment c = QComment.comment;
  QRankingReview rankingReview = QRankingReview.rankingReview;

  @Override
  public List<Review> findAll(ReviewSearchCondition condition, int limit) {
    JPAQuery<Review> query = queryFactory
        .selectFrom(review)
        .join(review.user, user).fetchJoin()
        .join(review.book, book).fetchJoin();

    BooleanBuilder where = new BooleanBuilder();

    where.and(review.user.isDeleted.eq(false))
        .and(review.book.isDeleted.eq(false).and(review.isDeleted.eq(false)));

    if (condition.getUserId() != null) {
      where.and(review.user.id.eq(condition.getUserId()));
    }
    if (condition.getBookId() != null) {
      where.and(review.book.id.eq(condition.getBookId()));
    }
    if (condition.getKeyword() != null) {
      String keyword = "%" + condition.getKeyword().toLowerCase() + "%";
      where.and(
          review.content.lower().like(keyword)
              .or(user.nickname.lower().like(keyword))
              .or(book.title.lower().like(keyword))
              .or(review.content.lower().like(keyword))
      );
    }

    boolean isAsc = "ASC".equalsIgnoreCase(condition.getDirection());
    String orderBy = condition.getOrderBy();
    String cursor = condition.getCursor();
    LocalDateTime after = condition.getAfter();

    OrderSpecifier<?> primaryOrder;
    OrderSpecifier<?> secondOrder = null;

    if ("rating".equals(orderBy)) {
      primaryOrder = isAsc ? review.rating.asc() : review.rating.desc();
      secondOrder = isAsc ? review.createdAt.asc() : review.createdAt.desc();

      if (cursor != null && after != null) {
        int ratingCursor = (int) Double.parseDouble(cursor);
        where.and(
            isAsc
                ? review.rating.gt(ratingCursor)
                .or(review.rating.eq(ratingCursor).and(review.createdAt.gt(after)))
                : review.rating.lt(ratingCursor)
                    .or(review.rating.eq(ratingCursor).and(review.createdAt.lt(after)))
        );
      }
    } else { // createdAt
      primaryOrder = isAsc ? review.createdAt.asc() : review.createdAt.desc();

      if (after != null) {
        where.and(
            isAsc ? review.createdAt.gt(after)
                : review.createdAt.lt(after)
        );
      }
    }

    query.where(where)
        .limit(limit + 1);

    if(secondOrder != null) {
      query.orderBy(primaryOrder, secondOrder);
    }else {
      query.orderBy(primaryOrder);
    }

    return query.fetch();
  }

  public long countByCondition(ReviewSearchCondition condition) {
    BooleanBuilder where = new BooleanBuilder();

    where.and(review.user.isDeleted.eq(false));
    where.and(review.book.isDeleted.eq(false));
    where.and(review.isDeleted.eq(false));

    if (condition.getUserId() != null) {
      where.and(review.user.id.eq(condition.getUserId()));
    }
    if (condition.getBookId() != null) {
      where.and(review.book.id.eq(condition.getBookId()));
    }
    if (condition.getKeyword() != null) {
      String keyword = "%" + condition.getKeyword().toLowerCase() + "%";
      where.and(
          review.content.lower().like(keyword)
              .or(review.user.nickname.lower().like(keyword))
              .or(review.book.title.lower().like(keyword))
              .or(review.book.description.lower().like(keyword))
      );
    }

    Long res = queryFactory
        .select(review.count())
        .from(review)
        .where(where)
        .fetchOne();

    return res != null ? res : 0;
  }

  @Override
  public Map<UUID, Long> findLikeCountsByPeriod(LocalDateTime start, LocalDateTime end) {
    BooleanBuilder builder = new BooleanBuilder();
    if (start != null && end != null) {
      builder.and(rl.createdAt.between(start, end));
    }

    List<ReviewCountDto> likeCounts = queryFactory
        .select(Projections.constructor(ReviewCountDto.class, rl.review.id, rl.count()))
        .from(rl)
        .join(rl.review, review)
        .join(rl.user, user)
        .where(builder
            .and(review.user.isDeleted.eq(false))
            .and(review.book.isDeleted.eq(false))
            .and(review.isDeleted.eq(false))
            .and(user.isDeleted.eq(false))
        )
        .groupBy(rl.review.id)
        .fetch();

    //
    // log.info("해당 기간의 총 좋아요 수: {}", likeCounts.size());

    return likeCounts.stream()
        .collect(Collectors.toMap(
            ReviewCountDto::reviewId, ReviewCountDto::count
        ));
  }

  @Override
  public Map<UUID, Long> findCommentCountsByPeriod(LocalDateTime start, LocalDateTime end) {
    BooleanBuilder builder = new BooleanBuilder();
    if (start != null && end != null) {
      builder.and(c.createdAt.between(start, end));
    }

    List<ReviewCountDto> commentCounts = queryFactory
        .select(Projections.constructor(ReviewCountDto.class, c.review.id, c.count()))
        .from(c)
        .join(c.review, review)
        .join(review.user, user)
        .join(review.book, book)
        .where(
            builder.and(review.isDeleted.eq(false))
                .and(c.isDeleted.eq(false))
                .and(review.user.isDeleted.eq(false))
                .and(review.book.isDeleted.eq(false))
                .and(c.user.isDeleted.eq(false))
        )
        .groupBy(c.review.id)
        .fetch();

    // log.info("해당 기간의 총 댓글 수: {}", commentCounts.size());

    return commentCounts.stream()
        .collect(Collectors.toMap(
            ReviewCountDto::reviewId, ReviewCountDto::count
        ));
  }

  public List<RankingReview> findRankingReviewsByPeriod(RankingReviewRequest request, int limit) {
    String direction = request.getDirection();
    OrderSpecifier<?> createdAtOrder = direction.equalsIgnoreCase("ASC")
        ? rankingReview.reviewCreatedAt.asc()
        : rankingReview.reviewCreatedAt.desc();

    long offset = request.getCursor() != null
        ? Long.parseLong(request.getCursor())
        : 0;

    return queryFactory
        .selectFrom(rankingReview)
        .join(rankingReview.review, review).fetchJoin()
        .join(review.user, user).fetchJoin()
        .join(review.book, book).fetchJoin()
        .where(rankingReview.period.eq(request.getPeriod())
            .and(review.book.isDeleted.eq(false))
            .and(review.user.isDeleted.eq(false))
            .and(review.isDeleted.eq(false)))
        .orderBy(
            rankingReview.score.desc(),
            createdAtOrder
        )
        .offset(offset)
        .limit(limit + 1)
        .fetch();
  }

  public long countRakingReviewByPeriod(Period period) {
    Long res = queryFactory
        .select(rankingReview.count())
        .from(rankingReview)
        .join(rankingReview.review, review)
        .join(review.book, book)
        .join(review.user, user)
        .where(
            rankingReview.period.eq(period),
            review.isDeleted.eq(false),
            book.isDeleted.eq(false),
            user.isDeleted.eq(false)
        )
        .fetchOne();

    return res != null ? res : 0;
  }
}

