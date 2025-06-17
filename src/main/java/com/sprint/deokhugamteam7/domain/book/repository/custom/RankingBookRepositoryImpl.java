package com.sprint.deokhugamteam7.domain.book.repository.custom;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.dto.BookActivity;
import com.sprint.deokhugamteam7.domain.book.dto.BookDto;
import com.sprint.deokhugamteam7.domain.book.dto.PopularBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.condition.BookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.condition.PopularBookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponseBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponsePopularBookDto;
import com.sprint.deokhugamteam7.domain.book.entity.QBook;
import com.sprint.deokhugamteam7.domain.book.entity.QRankingBook;
import com.sprint.deokhugamteam7.domain.review.entity.QReview;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class RankingBookRepositoryImpl implements RankingBookRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final QRankingBook rankingBook = QRankingBook.rankingBook;
  private final QBook book = QBook.book;
  private final QReview review = QReview.review;

  @Override
  public CursorPageResponseBookDto findAllByKeyword(BookCondition cond) {
    // 1) 기본 where 절 조립
    List<BooleanExpression> where = buildBookConditions(cond);
    if (StringUtils.hasText(cond.getKeyword())) {
      where.add(
          book.title.containsIgnoreCase(cond.getKeyword())
              .or(book.author.containsIgnoreCase(cond.getKeyword()))
              .or(book.isbn.containsIgnoreCase(cond.getKeyword()))
      );
    }

    // 2) 정렬조건
    OrderSpecifier<?> order = buildOrder(cond.getOrderBy(), cond.getDirection());

    // 3) 메인 쿼리: Book + 서브쿼리로 리뷰통계 가져오기
    List<BookDto> content = queryFactory
        .select(Projections.constructor(BookDto.class,
            book.id,
            book.title,
            book.author,
            book.description,
            book.publisher,
            book.publishedDate,
            book.isbn,
            book.thumbnailUrl,

            // 리뷰 개수 서브쿼리
            JPAExpressions.select(review.id.countDistinct())
                .from(review)
                .where(
                    review.book.id.eq(book.id),
                    review.isDeleted.isFalse(),
                    review.user.isDeleted.isFalse()
                ),

            // 평균 평점 서브쿼리 (null → 0.0)
            JPAExpressions.select(review.rating.avg().coalesce(0.0))
                .from(review)
                .where(
                    review.book.id.eq(book.id),
                    review.isDeleted.isFalse(),
                    review.user.isDeleted.isFalse()
                ),

            book.createdAt,
            book.updatedAt
        ))
        .from(book)
        .where(where.toArray(BooleanExpression[]::new))
        .orderBy(order)
        .limit(cond.getLimit() + 1)
        .fetch();

    return CursorPageResponseBookDto.of(content, cond.getOrderBy(), cond.getLimit());
  }

  @Override
  public CursorPageResponsePopularBookDto findPopularBooks(PopularBookCondition cond) {
    List<BooleanExpression> where = buildPopularBookConditions(cond);

    where.add(
        rankingBook.reviewCount.gt(0L)
            .or(rankingBook.rating.gt(0.0))
    );

    List<PopularBookDto> find = queryFactory
        .select(Projections.constructor(PopularBookDto.class,
            rankingBook.id,
            book.id,
            book.createdAt,
            book.title,
            book.author,
            book.thumbnailUrl,
            rankingBook.period.stringValue(),
            rankingBook.rank,
            rankingBook.score,
            rankingBook.reviewCount,
            rankingBook.rating))
        .from(rankingBook)
        .join(rankingBook.book, book)
        .where(where.toArray(BooleanExpression[]::new))
        .orderBy(buildOrder("score", cond.getDirection()))
        .limit(cond.getLimit() + 1)
        .fetch();
    return CursorPageResponsePopularBookDto.of(find, cond.getLimit());
  }

  @Override
  public List<BookActivity> findReviewActivitySummary(LocalDateTime start, LocalDateTime end) {
    BooleanBuilder onCond = new BooleanBuilder()
        .and(review.book.eq(book))
        .and(review.isDeleted.isFalse())
        .and(review.user.isDeleted.isFalse());

    BooleanBuilder whereCond = new BooleanBuilder()
        .and(book.isDeleted.isFalse());

    if (start != null) onCond.and(review.createdAt.goe(start));
    if (end   != null) onCond.and(review.createdAt.lt(end));

    NumberExpression<Long> reviewCnt   = review.id.countDistinct().coalesce(0L);
    NumberExpression<Integer> ratingSum = review.rating.sum().coalesce(0);

    return queryFactory
        .select(Projections.constructor(BookActivity.class,
            book.id,
            reviewCnt,
            ratingSum
        ))
        .from(book)
        .leftJoin(review).on(onCond)
        .where(whereCond)
        .groupBy(book.id)
        .fetch();
  }

//  조건 계산식
  private List<BooleanExpression> buildBookConditions(BookCondition c) {
    List<BooleanExpression> list = new ArrayList<>();
    list.add(book.isDeleted.isFalse());

    if (c.getAfter() != null) {
      list.add(book.createdAt.goe(c.getAfter()));
    }

    if (c.getCursor() != null) {
      list.add(buildCursorCondition(c.getOrderBy(), c.getDirection(), c.getCursor()));
    }
    return list;
  }

  private List<BooleanExpression> buildPopularBookConditions(PopularBookCondition c) {
    List<BooleanExpression> list = new ArrayList<>();
    list.add(book.isDeleted.isFalse());
    list.add(rankingBook.period.eq(Period.valueOf(c.getPeriod())));

    if (c.getCursor() != null) {
      list.add(buildCursorCondition("score", c.getDirection(), c.getCursor()));
    }
    return list;
  }

  private OrderSpecifier<?> buildOrder(String sortField, String direction) {
    boolean asc = "asc".equalsIgnoreCase(direction);
    return switch (sortField) {
      case "publishedDate" -> asc ? book.publishedDate.asc() : book.publishedDate.desc();
      case "rating" -> asc ? review.rating.avg().asc() : review.rating.avg().desc();
      case "reviewCount" -> asc ? review.id.countDistinct().asc() : review.id.countDistinct().desc();
      case "score" -> asc ? rankingBook.score.asc() : rankingBook.score.desc();
      default -> asc ? book.title.asc() : book.title.desc();
    };
  }

  private BooleanExpression buildCursorCondition(String sortField, String direction,
      String cursor) {
    boolean asc = "asc".equalsIgnoreCase(direction);

    switch (sortField) {
      case "title":
        return asc ? book.title.gt(cursor) : book.title.lt(cursor);
      case "publishedDate":
        LocalDate date = LocalDate.parse(cursor);
        return asc ? book.publishedDate.gt(date) : book.publishedDate.lt(date);
      case "rating":
        Double rating = Double.valueOf(cursor);
        return asc ? review.rating.avg().gt(rating) : review.rating.avg().lt(rating);
      case "reviewCount":
        Long cnt = Long.valueOf(cursor);
        return asc ? review.id.countDistinct().gt(cnt) : review.id.countDistinct().lt(cnt);
      case "score":     // 인기 검색용
      default:
        Double score = Double.valueOf(cursor);
        return asc ? rankingBook.score.gt(score) : rankingBook.score.lt(score);
    }
  }
}
