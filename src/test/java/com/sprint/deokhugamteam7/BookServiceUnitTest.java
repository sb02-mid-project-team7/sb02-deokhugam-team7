package com.sprint.deokhugamteam7;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.domain.book.dto.BookDto;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookCreateRequest;
import com.sprint.deokhugamteam7.domain.book.repository.BookRepository;
import com.sprint.deokhugamteam7.domain.book.service.BasicBookService;
import com.sprint.deokhugamteam7.domain.book.service.ImageService;
import com.sprint.deokhugamteam7.exception.DeokhugamException;
import java.time.LocalDate;
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

  @Test
  void createBookSuccess_WithNecessaryElement() {
    // given
    LocalDate now = LocalDate.now();
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
  void createBookSuccess_WithAll() {
    // given
    LocalDate now = LocalDate.now();
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

}
