package com.sprint.deokhugamteam7.domain.book;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.exception.DeokhugamException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class BookEntityTest {

  @Test
  void createBookWithNecessaryElement() {
    // given
    LocalDate now = LocalDate.now();
    // when
    Book book = Book.create("aaa", "bbb", "ccc", now).build();
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
    // given
    LocalDate now = LocalDate.now();
    // when
    Book book = Book.create("aaa", "bbb", "ccc", now).isbn("123").description("ddd")
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
  void createBookWithNoElement_ShouldThrowException() {
    assertThrows(DeokhugamException.class, () ->
        Book.create("", "", "", null).build()
    );
  }

  @Test
  void updateBook() {
    // given
    LocalDate now = LocalDate.now();
    Book book = Book.create("aaa", "bbb", "ccc", now).build();
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

}
