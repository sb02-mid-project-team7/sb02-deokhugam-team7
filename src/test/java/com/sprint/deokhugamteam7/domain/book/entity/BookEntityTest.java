package com.sprint.deokhugamteam7.domain.book.entity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.exception.DeokhugamException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BookEntityTest {

  private LocalDate now;
  private Book book;

  @BeforeEach
  void setUp() {
    now = LocalDate.now();
    book = Book.create("aaa", "bbb", "ccc", now).build();
  }

  @Test
  void createBookWithNecessaryElement() {
    // then
    assertAll(
        () -> assertEquals("aaa", book.getTitle()),
        () -> assertEquals("bbb", book.getAuthor()),
        () -> assertEquals("ccc", book.getPublisher()),
        () -> assertEquals(now, book.getPublishedDate())
    );
  }

  @Test
  void createBookWithAllElement() {
    // when
    book = Book.create("aaa", "bbb", "ccc", now).isbn("123").description("ddd")
        .thumbnailUrl("eee").build();
    // then
    assertAll(
        () -> assertEquals("aaa", book.getTitle()),
        () -> assertEquals("bbb", book.getAuthor()),
        () -> assertEquals("ccc", book.getPublisher()),
        () -> assertEquals(now, book.getPublishedDate()),
        () -> assertEquals("ddd", book.getDescription()),
        () -> assertEquals("123", book.getIsbn())
    );
  }

  @Test
  void createBookWithNoTitle_ShouldThrowException() {
    assertThrows(DeokhugamException.class, () ->
        Book.create(null, "a", "b", now).build()
    );
  }

  @Test
  void createBookWithNoAuthor_ShouldThrowException() {
    assertThrows(DeokhugamException.class, () ->
        Book.create("a", null, "b", now).build()
    );
  }

  @Test
  void createBookWithNoPublisher_ShouldThrowException() {
    assertThrows(DeokhugamException.class, () ->
        Book.create("a", "b", null, now).build()
    );
  }

  @Test
  void createBookWithNoPublishedDate_ShouldThrowException() {
    assertThrows(DeokhugamException.class, () ->
        Book.create("a", "b", "c", null).build()
    );
  }

  @Test
  void updateBook() {
    // when
    LocalDate newDay = now.plusDays(1L);
    book.update("a", "b", "c", "d", newDay, "e");
    // then
    assertAll(
        () -> assertEquals("a", book.getTitle()),
        () -> assertEquals("b", book.getAuthor()),
        () -> assertEquals("c", book.getDescription()),
        () -> assertEquals("d", book.getPublisher()),
        () -> assertEquals(newDay, book.getPublishedDate()),
        () -> assertEquals("e", book.getThumbnailUrl())
    );
  }

  @Test
  void updateBook_WithNullUpdate() {
    // when
    book.update(null, null, null, null, null, null);
    // then
    assertAll(
        () -> assertEquals("aaa", book.getTitle()),
        () -> assertEquals("bbb", book.getAuthor()),
        () -> assertNull(book.getDescription()),
        () -> assertEquals("ccc", book.getPublisher()),
        () -> assertEquals(now, book.getPublishedDate()),
        () -> assertNull(book.getThumbnailUrl())
    );
  }

  @Test
  void getReviewTest() {
    // given
    User user = mock(User.class);
    Review review = Review.create(book, user, "test", 3);
    book.setReviews(List.of(review));
    // when
    List<Review> reviews = book.getReviews();
    // then
    assertAll(
        () -> assertNotNull(reviews),
        () -> assertEquals(1, reviews.size()),
        () -> assertEquals("test", reviews.get(0).getContent())
    );
  }

}
