package com.sprint.deokhugamteam7.domain.review.service.basic;

import com.sprint.deokhugamteam7.constant.NotificationType;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.entity.RankingBook;
import com.sprint.deokhugamteam7.domain.book.repository.BookRepository;
import com.sprint.deokhugamteam7.domain.comment.repository.CommentRepository;
import com.sprint.deokhugamteam7.domain.notification.entity.Notification;
import com.sprint.deokhugamteam7.domain.notification.repository.NotificationRepository;
import com.sprint.deokhugamteam7.domain.review.dto.request.RankingReviewRequest;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewCreateRequest;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewSearchCondition;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewUpdateRequest;
import com.sprint.deokhugamteam7.domain.review.dto.response.CursorPageResponsePopularReviewDto;
import com.sprint.deokhugamteam7.domain.review.dto.response.CursorPageResponseReviewDto;
import com.sprint.deokhugamteam7.domain.review.dto.response.PopularReviewDto;
import com.sprint.deokhugamteam7.domain.review.dto.response.ReviewDto;
import com.sprint.deokhugamteam7.domain.review.dto.response.ReviewLikeDto;
import com.sprint.deokhugamteam7.domain.review.entity.RankingReview;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.review.entity.ReviewLike;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewLikeRepository;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewRepository;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewRepositoryCustom;
import com.sprint.deokhugamteam7.domain.review.service.ReviewService;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import com.sprint.deokhugamteam7.exception.review.ReviewException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicReviewService implements ReviewService {

  private final ReviewRepository reviewRepository;
  private final UserRepository userRepository;
  private final BookRepository bookRepository;
  private final ReviewLikeRepository reviewLikeRepository;
  private final CommentRepository commentRepository;
  private final ReviewRepositoryCustom reviewRepositoryCustom;
  private final NotificationRepository notificationRepository;

  @Override
  @Transactional
  public ReviewDto create(ReviewCreateRequest request) {
    UUID userId = request.userId();
    UUID bookId = request.bookId();

    User user = userRepository.findByIdAndIsDeletedFalse(userId)
        .orElseThrow(() -> new ReviewException(ErrorCode.USER_NOT_FOUND));
    Book book = bookRepository.findByIdAndIsDeletedFalse(bookId)
        .orElseThrow(() -> new ReviewException(ErrorCode.BOOK_NOT_FOUND));

    if (reviewRepository.existsByUserAndBook(user, book)) {
      throw new ReviewException(ErrorCode.REVIEW_ALREADY_EXISTS);
    }

    List<RankingBook> rankingBooks = book.getRankingBooks();
    rankingBooks.forEach(rankingBook -> rankingBook.update(request.rating(), false));

    Review review = Review.create(book, user, request.content(), request.rating());
    reviewRepository.save(review);

    log.info("[BasicReviewService] create Review: id {}, "
            + "userId {}, bookId {}, content {}, rating {}, isDeleted {},createdAt {}, updatedAt {}",
        review.getId(), user.getId(), book.getId(), review.getContent(),
        review.getRating(), review.getIsDeleted(), review.getCreatedAt(), review.getUpdatedAt());

    return ReviewDto.of(review, 0, 0, false);
  }

  @Override
  @Transactional
  public ReviewDto update(UUID id, UUID userId, ReviewUpdateRequest request) {
    if (!userRepository.existsById(userId)) {
      throw new ReviewException(ErrorCode.USER_NOT_FOUND);
    }
    Review review = reviewRepository.findByIdWithUserAndBook(id)
        .orElseThrow(() -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND));

    if (review.getUser().isDeleted() || review.getBook().getIsDeleted() || review.getIsDeleted()) {
      throw new ReviewException(ErrorCode.INTERNAL_BAD_REQUEST);
    }

    review.validateUserAuthorization(userId);

    String newContent = request.content();
    int beforeRating = review.getRating();
    int newRating = request.rating();

    review.update(newContent, newRating);

    List<RankingBook> rankingBooks = review.getBook().getRankingBooks();
    rankingBooks.forEach(RankingBook::reCalculate);

    int likeCount = reviewLikeRepository.countByReviewId(id);
    int commentCount = commentRepository.countByReviewIdAndIsDeletedFalse(id);
    boolean likedByMe = reviewLikeRepository.existsByUserIdAndReviewId(userId, id);

    log.info("[BasicReviewService] update Review: id {}, userId {}"
            + "content {}, rating {}, isDeleted {}, "
            + "updatedAt {}, likeCount {}, commentCount {} , likedByMe {}",
        review.getId(), userId, review.getContent(), review.getRating(),
        review.getIsDeleted(), review.getUpdatedAt(), likeCount, commentCount, likedByMe);

    return ReviewDto.of(review, likeCount, commentCount, likedByMe);
  }

  @Override
  @Transactional
  public void deleteSoft(UUID id, UUID userId) {
    Review review = reviewRepository.findById(id)
        .orElseThrow(() -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND));

    if (review.getIsDeleted()) {
      throw new ReviewException(ErrorCode.REVIEW_NOT_FOUND);
    }

    review.validateUserAuthorization(userId);

    review.delete();
    reviewRepository.save(review);
    log.info("[BasicReviewService] deleteSoft Review: isDeleted {}", review.getIsDeleted());

    List<RankingBook> rankingBooks = review.getBook().getRankingBooks();
    rankingBooks.forEach(rankingBook -> rankingBook.update(review.getRating(), true));
  }

  @Override
  @Transactional
  public void deleteHard(UUID id, UUID userId) {
    Review review = reviewRepository.findById(id)
        .orElseThrow(() -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND));

    review.validateUserAuthorization(userId);

    reviewRepository.delete(review);
    log.info("[BasicReviewService] deleteHard Review: isDeleted id {}, userId {}", review.getId(),
        userId);

    List<RankingBook> rankingBooks = review.getBook().getRankingBooks();
    rankingBooks.forEach(rankingBook -> rankingBook.update(review.getRating(), true));
  }

  @Override
  @Transactional(readOnly = true)
  public ReviewDto findById(UUID id, UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw new ReviewException(ErrorCode.USER_NOT_FOUND);
    }

    Review review = reviewRepository.findByIdWithUserAndBook(id)
        .orElseThrow(() -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND));

    int likeCount = reviewLikeRepository.countByReviewId(id);
    int commentCount = commentRepository.countByReviewIdAndIsDeletedFalse(id);
    boolean likedByMe = reviewLikeRepository.existsByUserIdAndReviewId(userId, id);

    return ReviewDto.of(review, likeCount, commentCount, likedByMe);
  }

  @Override
  @Transactional
  public ReviewLikeDto like(UUID id, UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ReviewException(ErrorCode.USER_NOT_FOUND));
    Review review = reviewRepository.findById(id)
        .orElseThrow(() -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND));

    Optional<ReviewLike> optional = reviewLikeRepository.findByReviewIdAndUserId(id, userId);
    boolean liked;

    if (optional.isPresent()) {
      reviewLikeRepository.delete(optional.get());
      liked = false;
    } else {
      liked = true;

      ReviewLike reviewLike = ReviewLike.create(user, review);
      reviewLikeRepository.save(reviewLike);
      log.info("[BasicReviewService] like Review: id {}, userId {}, like",
          review.getId(), userId);

      log.info("알림 생성 진행: userId: {}", review.getUser().getId());
      Notification notification = Notification.create(review.getUser(), review,
          NotificationType.LIKE.formatMessage(user, null));
      notificationRepository.save(notification);
      log.info("알림 생성 완료");
    }

    return new ReviewLikeDto(id, userId, liked);
  }

  @Override
  @Transactional(readOnly = true)
  public CursorPageResponseReviewDto findAll(ReviewSearchCondition condition, UUID headerUserId) {
    int limit = condition.getLimit();
    List<Review> res = reviewRepositoryCustom.findAll(condition, limit);

    boolean hasNext = res.size() > limit;
    List<Review> currentPage = hasNext ? res.subList(0, limit) : res;

    List<ReviewDto> reviewDtoList = currentPage.stream()
        .map(review -> {
          int likeCount = reviewLikeRepository.countByReviewId(review.getId());
          int commentCount = commentRepository.countByReviewIdAndIsDeletedFalse(review.getId());
          boolean likedByMe = reviewLikeRepository
              .existsByUserIdAndReviewId(headerUserId, review.getId());
          return ReviewDto.of(review, likeCount, commentCount, likedByMe);
        })
        .toList();

    String nextCursor = null;
    LocalDateTime nextAfter = null;

    if (hasNext) {
      Review last = currentPage.get(currentPage.size() - 1);
      nextAfter = last.getCreatedAt();

      if (condition.getOrderBy().equals("createdAt")) {
        nextCursor = nextAfter.toString();
      } else {
        double lastRating = last.getRating();
        nextCursor = Double.toString(lastRating);
      }
    }

    long totalElements = reviewRepositoryCustom.countByCondition(condition);

    return new CursorPageResponseReviewDto(
        reviewDtoList,
        nextCursor,
        nextAfter,
        limit,
        totalElements,
        hasNext
    );
  }

  @Override
  @Transactional(readOnly = true)
  public CursorPageResponsePopularReviewDto popular(RankingReviewRequest request) {
    List<RankingReview> rankingReviews
        = reviewRepositoryCustom.findRankingReviewsByPeriod(request, request.getLimit());

    boolean hasNext = rankingReviews.size() > request.getLimit();
    List<RankingReview> currentPage =
        hasNext ? rankingReviews.subList(0, request.getLimit()) : rankingReviews;

    long start = (request.getCursor() != null ?
        Long.parseLong(request.getCursor()) : 0) + 1;

    List<PopularReviewDto> content = IntStream.range(0, currentPage.size())
        .mapToObj(i -> {
          RankingReview ranking = currentPage.get(i);
          int likeCount = reviewLikeRepository.countByReviewId(ranking.getReview().getId());
          int commentCount
              = commentRepository.countByReviewIdAndIsDeletedFalse(ranking.getReview().getId());

          return PopularReviewDto.of(ranking, ranking.getReview(), start + i, likeCount,
              commentCount);
        }).toList();

    String nextCursor = hasNext ? String.valueOf(start + request.getLimit() - 1) : null;
    LocalDateTime nextAfter = null;

    if (hasNext) {
      RankingReview last = currentPage.get(currentPage.size() - 1);
      nextAfter = last.getReviewCreatedAt();
    }
    log.info(
        "[BasicReviewService] popular Review: period  {}, direction {}, nextCursor {}, size{}, hasNext {}",
        request.getPeriod(), request.getDirection(), nextCursor, currentPage.size(), hasNext);

    return new CursorPageResponsePopularReviewDto(
        content,
        nextCursor,
        nextAfter,
        currentPage.size(),
        reviewRepositoryCustom.countRakingReviewByPeriod(request.getPeriod()),
        hasNext
    );
  }

}
