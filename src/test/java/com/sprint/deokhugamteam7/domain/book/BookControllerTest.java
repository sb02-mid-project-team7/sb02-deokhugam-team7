package com.sprint.deokhugamteam7.domain.book;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.deokhugamteam7.config.TestSecurityConfig;
import com.sprint.deokhugamteam7.domain.book.controller.BookController;
import com.sprint.deokhugamteam7.domain.book.dto.BookDto;
import com.sprint.deokhugamteam7.domain.book.dto.NaverBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookCreateRequest;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookUpdateRequest;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponseBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponsePopularBookDto;
import com.sprint.deokhugamteam7.domain.book.service.APIService;
import com.sprint.deokhugamteam7.domain.book.service.BarcodeService;
import com.sprint.deokhugamteam7.domain.book.service.BookSearchService;
import com.sprint.deokhugamteam7.domain.book.service.BookService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(BookController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
public class BookControllerTest {

  private final UUID BOOK_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
  private DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd");
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private APIService apiService;

  @MockitoBean
  private BarcodeService barcodeService;


  @MockitoBean
  private BookSearchService bookSearchService;

  @MockitoBean
  private BookService bookService;

  @Test
  void create_success() throws Exception {
    // given
    BookCreateRequest request = mock(BookCreateRequest.class);
    LocalDate date = LocalDate.parse("19990101", yyyyMMdd);
    BookDto bookDto = new BookDto(BOOK_ID, "제목", "저자", "설명", "출판사", date, "123", "이미지", 1L, 2.0, null,
        null);

    MockMultipartFile jsonPart = new MockMultipartFile(
        "bookData",
        null,
        "application/json",
        objectMapper.writeValueAsBytes(request)
    );
    when(bookService.create(any(), any())).thenReturn(bookDto);
    // when & then
    mockMvc.perform(multipart("/api/books")
            .file(jsonPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(BOOK_ID.toString()));
  }

  @Test
  void update_success() throws Exception {
    // given
    BookUpdateRequest updateRequest = mock(BookUpdateRequest.class);
    LocalDate date = LocalDate.parse("19990101", yyyyMMdd);
    BookDto bookDto = new BookDto(BOOK_ID, "제목", "저자", "설명", "출판사", date, "123", "이미지", 1L, 2.0, null,
        null);

    MockMultipartFile jsonPart = new MockMultipartFile(
        "bookData",
        null,
        "application/json",
        objectMapper.writeValueAsBytes(updateRequest)
    );

    when(bookService.update(eq(BOOK_ID), any(BookUpdateRequest.class), nullable(MultipartFile.class))).thenReturn(bookDto);
    // when & then
    mockMvc.perform(multipart("/api/books/{bookId}", BOOK_ID)
            .file(jsonPart)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .with(request -> {
              request.setMethod("PATCH");
              return request;
            }))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(BOOK_ID.toString()));
  }

  @Test
  void deleteLogically_Success() throws Exception {
    // when & then
    mockMvc.perform(delete("/api/books/{bookId}", BOOK_ID))
        .andExpect(status().isNoContent());
  }

  @Test
  void deletePhysically_Success() throws Exception {
    // when & then
    mockMvc.perform(delete("/api/books/{bookId}/hard", BOOK_ID))
        .andExpect(status().isNoContent());
  }

  @Test
  void info_Success() throws Exception {
    // given
    LocalDate date = LocalDate.parse("19990101", yyyyMMdd);
    NaverBookDto naverBookDto = new NaverBookDto("제목", "저자", "설명", "출판", date, "123", null);
    when(apiService.searchBooks("123")).thenReturn(naverBookDto);
    // when & then
    mockMvc.perform(get("/api/books/info").queryParam("isbn", "123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("제목"))
        .andExpect(jsonPath("$.author").value("저자"))
        .andExpect(jsonPath("$.isbn").value("123"));
  }

  @Test
  void extractIsbn_Success() throws Exception{
    // given
    MockMultipartFile mockFile = new MockMultipartFile("image", "test.png", "image/png",
        "dummy".getBytes());
    when(barcodeService.extractIsbn(mockFile)).thenReturn("123456");
    // when
    mockMvc.perform(multipart("/api/books/isbn/ocr")
            .file(mockFile)
            .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isOk())
        .andExpect(content().string("123456"))
        .andDo(print());
  }

  @Test
  void findAll() throws Exception{
    // given
    CursorPageResponseBookDto dto = mock(CursorPageResponseBookDto.class);
    when(bookSearchService.findAll(any())).thenReturn(dto);
    // when & then
    mockMvc.perform(get("/api/books")
            .param("keyword","java")
        ).andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  void findPopularBooks() throws Exception{
    // given
    CursorPageResponsePopularBookDto dto = mock(CursorPageResponsePopularBookDto.class);
    when(bookSearchService.findPopularBooks(any())).thenReturn(dto);
    // when & then
    mockMvc.perform(get("/api/books")).andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  void findById() throws Exception{
    // given
    UUID id = UUID.randomUUID();
    BookDto dto = mock(BookDto.class);
    when(bookService.findById(id)).thenReturn(dto);
    // when & then
    mockMvc.perform(get("/api/books/{bookId}", id))
        .andExpect(status().isOk())
        .andDo(print());
  }

}
