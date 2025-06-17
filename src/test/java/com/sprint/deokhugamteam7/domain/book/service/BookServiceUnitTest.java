package com.sprint.deokhugamteam7.domain.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.domain.book.dto.BookDto;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookCreateRequest;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookUpdateRequest;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.repository.BookRepository;
import com.sprint.deokhugamteam7.exception.DeokhugamException;
import com.sprint.deokhugamteam7.exception.book.BookException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
public class BookServiceUnitTest {

  @Mock
  private S3ImageComponent s3ImageService;

  @Mock
  private BookRepository bookRepository;

  @InjectMocks
  private BasicBookService bookService;


  @Test
  void create_Success_WithNecessaryElement() {
    // given
    LocalDate now = LocalDate.now();
    BookCreateRequest request = new BookCreateRequest("aaa", "bbb", null, "ccc", now, "123");
    // when
    BookDto bookDto = bookService.create(request, null);
    // then
    assertAll(
        ()->assertThat(bookDto.title()).isEqualTo("aaa"),
        ()-> assertThat(bookDto.author()).isEqualTo("bbb"),
        ()-> assertThat(bookDto.publisher()).isEqualTo("ccc"),
        ()-> assertThat(bookDto.publishedDate()).isEqualTo(now)
    );
  }

  @Test
  void create_Success_WithAll() {
    // given
    LocalDate now = LocalDate.now();
    MockMultipartFile mockMultipartFile = new MockMultipartFile("name", "test.png", "image/png",
        new byte[0]);
    BookCreateRequest request = new BookCreateRequest("aaa", "bbb", "ccc", "ddd", now, "11111111");
    when(s3ImageService.uploadImage(mockMultipartFile)).thenReturn("testUrl");
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
  void create_WithSameIsbn_ShouldThrowException() {
    // given
    LocalDate now = LocalDate.now();
    BookCreateRequest request = new BookCreateRequest("aaa", "bbb", "ccc", "ddd", now, "1234567");
    Book mock = Book.create("aaa", "bbb", "ddd", now).isbn("1234567").build();
    when(bookRepository.findByIsbn("1234567")).thenReturn(Optional.of(mock));
    // when & then
    assertThrows(DeokhugamException.class, ()->
        bookService.create(request, mock(MockMultipartFile.class)));
  }

  @Test
  void update_Success() {
    // given
    LocalDate now = LocalDate.now();
    UUID id = UUID.randomUUID();
    LocalDate newDate = LocalDate.now().plusDays(1);
    Book book = Book.create("aaa", "bbb", "ccc", now).build();
    BookUpdateRequest request = new BookUpdateRequest("test111", "test222", "test333",
        "test444", newDate);
    when(bookRepository.findByIdAndIsDeletedFalse(id)).thenReturn(Optional.of(book));
    // when
    BookDto bookDto = bookService.update(id, request, null);
    // then
    assertAll(
        ()->assertThat(bookDto.title()).isEqualTo("test111"),
        ()-> assertThat(bookDto.author()).isEqualTo("test222"),
        ()-> assertThat(bookDto.description()).isEqualTo("test333"),
        ()-> assertThat(bookDto.publisher()).isEqualTo("test444"),
        ()->assertThat(bookDto.publishedDate()).isEqualTo(newDate)
    );
  }


  @Test
  void update_WithImage_Success() {
    // given
    LocalDate now = LocalDate.now();
    UUID id = UUID.randomUUID();
    LocalDate newDate = LocalDate.now().plusDays(1);
    MockMultipartFile mockFile = new MockMultipartFile("image", "test.png", "image/png",
        "dummy".getBytes());
    Book book = Book.create("aaa", "bbb", "ccc", now).build();
    BookUpdateRequest request = new BookUpdateRequest("test111", "test222", "test333",
        "test444", newDate);
    when(bookRepository.findByIdAndIsDeletedFalse(id)).thenReturn(Optional.of(book));
    // when
    BookDto bookDto = bookService.update(id, request, mockFile);
    // then
    assertAll(
        ()->assertThat(bookDto.title()).isEqualTo("test111"),
        ()-> assertThat(bookDto.author()).isEqualTo("test222"),
        ()-> assertThat(bookDto.description()).isEqualTo("test333"),
        ()-> assertThat(bookDto.publisher()).isEqualTo("test444"),
        ()->assertThat(bookDto.publishedDate()).isEqualTo(newDate)
    );
  }

  @Test
  void update_WithOutBook_Fail() {
    // given
    UUID id = UUID.randomUUID();
    LocalDate newDate = LocalDate.now().plusDays(1);
    BookUpdateRequest request = new BookUpdateRequest("test111", "test222", "test333",
        "test444", newDate);
    when(bookRepository.findByIdAndIsDeletedFalse(id)).thenReturn(Optional.empty());
    // when & then
    assertThrows(BookException.class, () -> bookService.update(id,request,null));
  }

  @Test
  void delete_Logically_Success() {
    // given
    LocalDate now = LocalDate.now();
    UUID id = UUID.randomUUID();
    Book book = Book.create("aaa", "bbb", "ccc", now).build();
    when(bookRepository.findById(id)).thenReturn(Optional.of(book));
    // when
    bookService.deleteLogically(id);
    // then
    assertThat(book.getIsDeleted()).isTrue();
    verify(bookRepository).findById(id);
    verify(bookRepository).save(book);
  }
  
  @Test
  void delete_Logically_WithoutBook_ShouldThrowException() {
    // given
    UUID id = UUID.randomUUID();
    when(bookRepository.findById(id)).thenReturn(Optional.empty());
    // when & then
    assertThrows(DeokhugamException.class,
        () -> bookService.deleteLogically(id));
  }

  @Test
  void delete_Physically_Success() {
    // given
    LocalDate now = LocalDate.now();
    UUID id = UUID.randomUUID();
    Book book = Book.create("aaa", "bbb", "ccc", now).build();
    when(bookRepository.findById(id)).thenReturn(Optional.of(book));
    // when
    bookService.deletePhysically(id);
    // then
    verify(bookRepository).findById(id);
    verify(bookRepository).delete(book);
  }

  @Test
  void delete_Physically_WithoutBook_ShouldThrowException() {
    // given
    UUID id = UUID.randomUUID();
    when(bookRepository.findById(id)).thenReturn(Optional.empty());
    // when & then
    assertThrows(DeokhugamException.class,
        () -> bookService.deletePhysically(id));
  }

  @Test
  void findById_Success() {
    // given
    LocalDate now = LocalDate.now();
    UUID id = UUID.randomUUID();
    Book book = Book.create("aaa", "bbb", "ccc", now).build();
    when(bookRepository.findByIdAndIsDeletedFalse(id)).thenReturn(Optional.of(book));
    // when
    BookDto bookDto = bookService.findById(id);
    // then
    assertAll(
        ()->assertThat(bookDto.title()).isEqualTo("aaa"),
        ()-> assertThat(bookDto.author()).isEqualTo("bbb"),
        ()-> assertThat(bookDto.publisher()).isEqualTo("ccc"),
        ()->assertThat(bookDto.publishedDate()).isEqualTo(now)
    );
  }

  @Test
  void findById_Fail() {
    // given
    UUID id = UUID.randomUUID();
    when(bookRepository.findByIdAndIsDeletedFalse(id)).thenReturn(Optional.empty());
    // when & then
    assertThrows(BookException.class,
        () -> bookService.findById(id));
  }

}
