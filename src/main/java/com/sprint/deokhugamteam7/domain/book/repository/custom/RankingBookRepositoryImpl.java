package com.sprint.deokhugamteam7.domain.book.repository.custom;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
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
    // 1) 기본 where 조건 (isDeleted, after, keyword)
    List<BooleanExpression> where = buildBookConditions(cond);

    // 2) 커서용 BooleanExpression
    BooleanExpression cursorCond = cond.getCursor() != null
        ? buildCursorCondition(cond.getOrderBy(), cond.getDirection(), cond.getCursor())
        : null;

    // 3) 쿼리 빌드: select → from → join → where → groupBy
    JPAQuery<BookDto> query = queryFactory
        .select(com.querydsl.core.types.Projections.constructor(
            BookDto.class,
            book.id,
            book.title,
            book.author,
            book.description,
            book.publisher,
            book.publishedDate,
            book.isbn,
            book.thumbnailUrl,
            review.id.countDistinct(),               // reviewCount
            review.rating.avg().coalesce(0.0),       // ratingAvg
            book.createdAt,
            book.updatedAt
        ))
        .from(book)
        .leftJoin(book.reviews, review)
        .on(review.isDeleted.isFalse(), review.user.isDeleted.isFalse())
        .where(where.toArray(new BooleanExpression[0]))
        .groupBy(
            book.id,
            book.title,
            book.author,
            book.description,
            book.publisher,
            book.publishedDate,
            book.isbn,
            book.thumbnailUrl,
            book.createdAt,
            book.updatedAt
        );

    // 4) 커서 필터링: Aggregate 필드는 having(), 나머지는 where()
    if (cursorCond != null) {
      if (isAggregateSortField(cond.getOrderBy())) {
        query.having(cursorCond);
      } else {
        query.where(cursorCond);
      }
    }

    // 5) Tie-breaker: ID 오름/내림
    OrderSpecifier<?> tieBreaker = "asc".equalsIgnoreCase(cond.getDirection())
        ? book.id.asc()
        : book.id.desc();

    // 6) 정렬 스펙 생성
    List<OrderSpecifier<?>> orderSpecs = new ArrayList<>();

    if ("rating".equals(cond.getOrderBy())) {
      // 평균 평점 (null → 0.0)
      NumberExpression<Double> avgRating = review.rating.avg().coalesce(0.0);

      // 0점 플래그: ASC일 땐 0점 → 0, 나머지 → 1 (맨 앞으로)
      //            DESC일 땐 0점 → 1, 나머지 → 0 (맨 뒤로)
      NumberExpression<Integer> zeroFlag = "asc".equalsIgnoreCase(cond.getDirection())
          ? Expressions.cases().when(avgRating.eq(0.0)).then(0).otherwise(1)
          : Expressions.cases().when(avgRating.eq(0.0)).then(1).otherwise(0);

      // 1) 0점 플래그 → 2) 평점 → 3) ID 순
      orderSpecs.add(zeroFlag.asc());
      orderSpecs.add("asc".equalsIgnoreCase(cond.getDirection())
          ? avgRating.asc()
          : avgRating.desc());
    } else {
      // 평점 외 필드는 기존 동적 buildOrder()
      orderSpecs.add(buildOrder(cond.getOrderBy(), cond.getDirection()));
    }

    orderSpecs.add(tieBreaker);

    // 7) 최종 fetch
    List<BookDto> content = query
        .orderBy(orderSpecs.toArray(new OrderSpecifier<?>[0]))
        .limit(cond.getLimit() + 1)
        .fetch();

    return CursorPageResponseBookDto.of(content, cond.getOrderBy(), cond.getLimit());
  }

  // — 커서 대상이 Aggregate 필드인지 확인
  private boolean isAggregateSortField(String sortField) {
    return List.of("rating", "reviewCount", "score").contains(sortField);
  }

  // — buildOrder: cond.getOrderBy()에 따라 동적 Primary Order 반환
  private OrderSpecifier<?> buildOrder(String sortField, String direction) {
    boolean asc = "asc".equalsIgnoreCase(direction);
    return switch (sortField) {
      case "publishedDate" -> asc
          ? book.publishedDate.asc()
          : book.publishedDate.desc();
      case "rating"        -> asc
          ? review.rating.avg().asc()
          : review.rating.avg().desc();
      case "reviewCount"   -> asc
          ? review.id.countDistinct().asc()
          : review.id.countDistinct().desc();
      case "rank"         -> asc
          ? rankingBook.rank.asc()
          : rankingBook.rank.desc();
      default              -> asc
          ? book.title.asc()
          : book.title.desc();
    };
  }

  // — buildCursorCondition: sortField별 커서 비교식 생성
  private BooleanExpression buildCursorCondition(
      String sortField, String direction, String cursor
  ) {
    boolean asc = "asc".equalsIgnoreCase(direction);
    switch (sortField) {
      case "title":
        return asc
            ? book.title.gt(cursor)
            : book.title.lt(cursor);

      case "publishedDate":
        LocalDate date = LocalDate.parse(cursor);
        return asc
            ? book.publishedDate.gt(date)
            : book.publishedDate.lt(date);

      case "rating":
        Double rating = Double.valueOf(cursor);
        return asc
            ? review.rating.avg().gt(rating)
            : review.rating.avg().lt(rating);

      case "reviewCount":
        Long cnt = Long.valueOf(cursor);
        return asc
            ? review.id.countDistinct().gt(cnt)
            : review.id.countDistinct().lt(cnt);

      case "score":
        Double score = Double.valueOf(cursor);
        return asc
            ? rankingBook.score.gt(score)
            : rankingBook.score.lt(score);

      case "rank":
        Long rank = Long.valueOf(cursor);
        return asc
            ? rankingBook.rank.gt(rank)
            : rankingBook.rank.lt(rank);

      default:
        // title 기본 케이스
        return asc
            ? book.title.gt(cursor)
            : book.title.lt(cursor);
    }
  }

  @Override
  public CursorPageResponsePopularBookDto findPopularBooks(PopularBookCondition cond) {
    // 1) 필터 리스트 준비
    List<BooleanExpression> where = new ArrayList<>();
    where.add(book.isDeleted.isFalse());
    where.add(rankingBook.period.eq(Period.valueOf(cond.getPeriod())));
    where.add(rankingBook.reviewCount.gt(0L)
        .or(rankingBook.rating.gt(0.0)));

    // 2) cursor가 넘어왔을 때만 rank 필터 적용 (오름차순 기준)
    if (cond.getCursor() != null) {
      where.add(rankingBook.rank.gt(Long.valueOf(cond.getCursor())));
    }

    // 3) 조회 + 고정 오름차순 정렬 + tie-breaker
    List<PopularBookDto> results = queryFactory
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
        .where(where.toArray(new BooleanExpression[0]))
        .orderBy(rankingBook.rank.asc(), book.id.asc())
        .limit(cond.getLimit() + 1)
        .fetch();

    return CursorPageResponsePopularBookDto.of(results, cond.getLimit());
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

  // — 기존의 키워드·after·isDeleted 조건
  private List<BooleanExpression> buildBookConditions(BookCondition c) {
    List<BooleanExpression> list = new ArrayList<>();
    list.add(book.isDeleted.isFalse());

    if (c.getAfter() != null) {
      list.add(book.createdAt.goe(c.getAfter()));
    }
    if (StringUtils.hasText(c.getKeyword())) {
      list.add(
          book.title.containsIgnoreCase(c.getKeyword())
              .or(book.author.containsIgnoreCase(c.getKeyword()))
              .or(book.isbn.containsIgnoreCase(c.getKeyword()))
      );
    }
    return list;
  }
}
