package com.sprint.deokhugamteam7.domain.review.service.basic;

import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.repository.BookRepository;
import com.sprint.deokhugamteam7.domain.comment.repository.CommentRepository;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewCreateRequest;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewUpdateRequest;
import com.sprint.deokhugamteam7.domain.review.dto.response.ReviewDto;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewLikeRepository;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewRepository;
import com.sprint.deokhugamteam7.domain.review.service.ReviewService;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import com.sprint.deokhugamteam7.exception.review.ReviewException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicReviewService implements ReviewService {

  private final ReviewRepository reviewRepository;
  private final UserRepository userRepository;
  private final BookRepository bookRepository;
  private final ReviewLikeRepository reviewLikeRepository;
  private final CommentRepository commentRepository;

  @Override
  @Transactional
  public ReviewDto create(ReviewCreateRequest request) {
    UUID userId = request.userId();
    UUID bookId = request.bookId();

    User user = userRepository.findByIdAndIsDeletedFalse(userId)
        .orElseThrow(() -> new ReviewException(ErrorCode.INTERNAL_SERVER_ERROR));
    Book book = bookRepository.findByIdAndIsDeletedFalse(bookId)
        .orElseThrow(() -> new ReviewException(ErrorCode.INTERNAL_SERVER_ERROR));

    Review review = Review.create(book, user, request.content(), request.rating());
    reviewRepository.save(review);

    return ReviewDto.of(review, 0, 0, false);
  }

  @Override
  @Transactional
  public ReviewDto update(UUID id, UUID userId, ReviewUpdateRequest request) {
    if (!userRepository.existsById(userId)) {
      throw new ReviewException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
    Review review = reviewRepository.findByIdWithUserAndBook(id)
        .orElseThrow(() -> new ReviewException(ErrorCode.INTERNAL_SERVER_ERROR));

    review.validateUserAuthorization(userId);

    if (review.getUser().isDeleted() || review.getBook().getIsDeleted()) {
      throw new ReviewException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    String newContent = request.content();
    int newRating = request.rating();

    review.update(newContent, newRating);

    int likeCount = reviewLikeRepository.countByReviewId(id);
    int commentCount = commentRepository.countByReviewIdAndIsDeletedFalse(id);
    boolean likedByMe = reviewLikeRepository.existsByUserIdAndReviewId(userId, id);

    return ReviewDto.of(review, likeCount, commentCount, likedByMe);
  }

  @Override
  @Transactional(readOnly = true)
  public void deleteSoft(UUID id, UUID userId) {
    Review review = reviewRepository.findById(id)
        .orElseThrow(() -> new ReviewException(ErrorCode.INTERNAL_SERVER_ERROR));

    review.validateUserAuthorization(userId);

    review.delete();
  }

  @Override
  @Transactional(readOnly = true)
  public void deleteHard(UUID id, UUID userId) {
    Review review = reviewRepository.findById(id)
        .orElseThrow(() -> new ReviewException(ErrorCode.INTERNAL_SERVER_ERROR));

    review.validateUserAuthorization(userId);

    reviewRepository.delete(review);
  }
}
