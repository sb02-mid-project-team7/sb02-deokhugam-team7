package com.sprint.deokhugamteam7;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.deokhugamteam7.domain.book.dto.BookDto;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookCreateRequest;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookUpdateRequest;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.repository.BookRepository;
import com.sprint.deokhugamteam7.domain.book.service.BookService;
import com.sprint.deokhugamteam7.domain.book.service.ImageService;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BookIntegrationTest {

  @MockitoBean
  private ImageService imageService;

  @Autowired
  private BookService bookService;

  @Autowired
  private BookRepository bookRepository;

  private LocalDate now;
  private Book testBook;

  @BeforeEach
  void setUp() {
    now = LocalDate.now();
    testBook = Book.create("test111", "test222", "test333", now).build();
    bookRepository.save(testBook);
  }

  @Test
  void createBookSuccess() {
    // given
    BookCreateRequest request = new BookCreateRequest("aaa", "bbb", null, "ccc", now, null);
    // when
    BookDto bookDto = bookService.create(request, null);
    // then
    assertAll(
        () -> assertThat(bookDto.id()).isNotNull(),
        () -> assertThat(bookDto.createdAt()).isNotNull(),
        ()-> assertThat(bookDto.updatedAt()).isNotNull(),
        () -> assertThat(bookDto.title()).isEqualTo("aaa"),
        () -> assertThat(bookDto.author()).isEqualTo("bbb"),
        () -> assertThat(bookDto.publisher()).isEqualTo("ccc"),
        () -> assertThat(bookDto.publishedDate()).isEqualTo(now)
    );
  }

  @Test
  void updateBookSuccess() {
    // given
    LocalDate newDate = LocalDate.now().plusDays(1);
    BookUpdateRequest request = new BookUpdateRequest("111", "222", "333", "444", newDate);
    // when
    BookDto bookDto = bookService.update(testBook.getId(), request, null);
    // then
    assertAll(
        () -> assertThat(bookDto.id()).isNotNull(),
        () -> assertThat(bookDto.createdAt()).isNotNull(),
        ()-> assertThat(bookDto.updatedAt()).isNotNull(),
        () -> assertThat(bookDto.title()).isEqualTo("111"),
        () -> assertThat(bookDto.author()).isEqualTo("222"),
        ()-> assertThat(bookDto.description()).isEqualTo("333"),
        () -> assertThat(bookDto.publisher()).isEqualTo("444"),
        () -> assertThat(bookDto.publishedDate()).isEqualTo(newDate)
    );
  }

}
