package com.sprint.deokhugamteam7.domain.review.entity;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.exception.review.ReviewException;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class ReviewTest {

  private User user;
  private User user2;
  private Book book;
  private Review review;

  @BeforeEach
  void setUp() {
    user = User.create("test@gmail.com", "test", "test1234!");
    UUID userId = UUID.randomUUID();

    user2 = User.create("test2@gmail.com", "test2", "test1234!");
    UUID userId2 = UUID.randomUUID();

    book = Book.create("책1", "책1", "책1", LocalDate.of(2020, 1, 15)).build();
    UUID bookId = UUID.randomUUID();

    ReflectionTestUtils.setField(user, "id", userId);
    ReflectionTestUtils.setField(user2, "id", userId2);
    ReflectionTestUtils.setField(book, "id", bookId);

    review = Review.create(book, user, "리뷰1", 3);
    UUID reviewId = UUID.randomUUID();
    ReflectionTestUtils.setField(review, "id", reviewId);
  }

  @Test
  @DisplayName("리뷰 접근 권한 검사 - 리뷰 작성자와 요청자 일치")
  void validateUserAuthorization_success() {
    assertDoesNotThrow(() -> review.validateUserAuthorization(user.getId()));
  }

  @Test
  @DisplayName("리뷰 접근 권한 검사 - 리뷰 작성자와 요청자 불일치")
  void validateUserAuthorization_fail() {
    assertThatThrownBy(() -> review.validateUserAuthorization(user2.getId()))
        .isInstanceOf(ReviewException.class);
  }

}
