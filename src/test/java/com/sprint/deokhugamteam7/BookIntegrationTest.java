package com.sprint.deokhugamteam7;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.deokhugamteam7.domain.book.dto.BookDto;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookCreateRequest;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookUpdateRequest;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.repository.BookRepository;
import com.sprint.deokhugamteam7.domain.book.service.BookService;
import com.sprint.deokhugamteam7.domain.book.service.S3ImageService;
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
  private S3ImageService s3ImageService;

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
  void createSuccess() {
    // given
    BookCreateRequest request = new BookCreateRequest("aaaa", "bbbb", null, "cccc", now, null);
    // when
    BookDto bookDto = bookService.create(request, null);
    // then
    assertAll(
        () -> assertThat(bookDto.id()).isNotNull(),
        () -> assertThat(bookDto.createdAt()).isNotNull(),
        ()-> assertThat(bookDto.updatedAt()).isNotNull(),
        () -> assertThat(bookDto.title()).isEqualTo("aaaa"),
        () -> assertThat(bookDto.author()).isEqualTo("bbbb"),
        () -> assertThat(bookDto.publisher()).isEqualTo("cccc"),
        () -> assertThat(bookDto.publishedDate()).isEqualTo(now)
    );
  }

  @Test
  void updateSuccess() {
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
  
  @Test
  void delete_Logically_Success() {
   // when
    bookService.deleteLogically(testBook.getId());
    // then
    assertTrue(bookRepository.findById(testBook.getId()).isPresent());
    assertTrue(testBook.getIsDeleted());
  }
  
  @Test
  void delete_Physical_Success() {
    // when
    bookService.deletePhysically(testBook.getId());
    // then
    assertFalse(bookRepository.findById(testBook.getId()).isPresent());
  }

}
