package com.sprint.deokhugamteam7.domain.review.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.deokhugamteam7.domain.book.entity.QBook;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewSearchCondition;
import com.sprint.deokhugamteam7.domain.review.entity.QReview;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.user.entity.QUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

  @PersistenceContext
  private EntityManager em;

  @Override
  public List<Review> findAll(ReviewSearchCondition condition, int limit) {
    QReview review = QReview.review;
    QUser user = QUser.user;
    QBook book = QBook.book;

    JPAQuery<Review> query = new JPAQueryFactory(em)
        .selectFrom(review)
        .join(review.user, user).fetchJoin()
        .join(review.book, book).fetchJoin();

    BooleanBuilder where = new BooleanBuilder();

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
              .or(book.description.lower().like(keyword))
      );
    }

    String orderby = condition.getOrderBy();
    String cursor = condition.getCursor();
    LocalDateTime after = condition.getAfter();

    if ("rating".equals(orderby)) {
      if (cursor != null && after != null) {
        int ratingCursor = (int) Double.parseDouble(cursor);
        where.and(
            review.rating.lt(ratingCursor)
                .or(review.rating.eq(ratingCursor))
                .and(review.createdAt.lt(after))
        );
      }
      query.orderBy(review.rating.desc(), review.createdAt.desc());
    } else {
      if (after != null) {
        where.and(review.createdAt.lt(after));
      }
      query.orderBy(review.createdAt.desc());
    }

    query.where(where)
        .limit(limit + 1);

    return query.fetch();
  }

  public long countByCondition(ReviewSearchCondition condition) {
    QReview review = QReview.review;

    BooleanBuilder where = new BooleanBuilder();

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

    Long res = new JPAQueryFactory(em)
        .select(review.count())
        .from(review)
        .where(where)
        .fetchOne();

    return res != null ? res : 0;
  }
}
