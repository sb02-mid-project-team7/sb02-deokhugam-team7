package com.sprint.deokhugamteam7;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.domain.book.dto.BookDto;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookCreateRequest;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookUpdateRequest;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.repository.BookRepository;
import com.sprint.deokhugamteam7.domain.book.service.BasicBookService;
import com.sprint.deokhugamteam7.domain.book.service.ImageService;
import com.sprint.deokhugamteam7.exception.DeokhugamException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
public class BookServiceUnitTest {

  @Mock
  private ImageService imageService;

  @Mock
  private BookRepository bookRepository;

  @InjectMocks
  private BasicBookService bookService;
  LocalDate now;
  @BeforeEach
  void setUp() {
    now = LocalDate.now();
  }

  @Test
  void createBook_Success_WithNecessaryElement() {
    // given
    BookCreateRequest request = new BookCreateRequest("aaa", "bbb", null, "ccc", now, null);
    // when
    BookDto bookDto = bookService.create(request, null);
    // then
    assertAll(
        ()->assertThat(bookDto.title()).isEqualTo("aaa"),
        ()-> assertThat(bookDto.author()).isEqualTo("bbb"),
        ()-> assertThat(bookDto.publisher()).isEqualTo("ccc"),
        ()->assertThat(bookDto.publishedDate()).isEqualTo(now)
    );
  }

  @Test
  void createBook_Success_WithAll() {
    // given
    MockMultipartFile mockMultipartFile = new MockMultipartFile("name", "test.png", "image/png",
        new byte[0]);
    BookCreateRequest request = new BookCreateRequest("aaa", "bbb", "ccc", "ddd", now, "11111111");
    when(imageService.uploadImage(mockMultipartFile)).thenReturn("testUrl");
    // when
    BookDto bookDto = bookService.create(request, mockMultipartFile);
    // then
    assertAll(
        () -> assertThat(bookDto.title()).isEqualTo("aaa"),
        () -> assertThat(bookDto.author()).isEqualTo("bbb"),
        () -> assertThat(bookDto.description()).isEqualTo("ccc"),
        () -> assertThat(bookDto.publisher()).isEqualTo("ddd"),
        () -> assertThat(bookDto.publishedDate()).isEqualTo(now),
        () -> assertThat(bookDto.isbn()).isEqualTo("11111111"),
        () -> assertThat(bookDto.thumbnailUrl()).isEqualTo("testUrl")
    );
  }

  @Test
  void createBook_WithSameIsbn_ShouldThrowException() {
    // given
    BookCreateRequest mock = mock(BookCreateRequest.class);
    when(mock.isbn()).thenReturn("1234567");
    when(bookRepository.existsByIsbn("1234567")).thenReturn(false);
    // when & then
    assertThrows(DeokhugamException.class, ()->
        bookService.create(mock, mock(MockMultipartFile.class)));
  }

  @Test
  void updateBook_Success() {
    // given
    UUID mock = UUID.randomUUID();
    LocalDate newDate = LocalDate.now().plusDays(1);
    Book book = Book.create("aaa", "bbb", "ccc", now).build();
    BookUpdateRequest request = new BookUpdateRequest("test111", "test222", "test333",
        "test444", newDate);
    when(bookRepository.findById(mock)).thenReturn(Optional.of(book));
    // when
    BookDto bookDto = bookService.update(mock, request, null);
    // then
    assertAll(
        ()->assertThat(bookDto.title()).isEqualTo("test111"),
        ()-> assertThat(bookDto.author()).isEqualTo("test222"),
        ()-> assertThat(bookDto.description()).isEqualTo("test333"),
        ()-> assertThat(bookDto.publisher()).isEqualTo("test444"),
        ()->assertThat(bookDto.publishedDate()).isEqualTo(newDate)
    );
  }

}
