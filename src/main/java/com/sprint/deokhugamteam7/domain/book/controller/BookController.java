package com.sprint.deokhugamteam7.domain.book.controller;

import com.sprint.deokhugamteam7.domain.book.dto.BookDto;
import com.sprint.deokhugamteam7.domain.book.dto.NaverBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.condition.BookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.condition.PopularBookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookCreateRequest;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookUpdateRequest;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponseBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponsePopularBookDto;
import com.sprint.deokhugamteam7.domain.book.service.APIService;
import com.sprint.deokhugamteam7.domain.book.service.BookSearchService;
import com.sprint.deokhugamteam7.domain.book.service.BookService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController implements BookApi {

  private final APIService apiService;
  private final BookService bookService;
  private final BookSearchService bookSearchService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BookDto> create(
      @RequestPart("bookData") BookCreateRequest request,
      @RequestPart(value = "thumbnailImage", required = false) MultipartFile file) {

    BookDto bookDto = bookService.create(request, file);

    return ResponseEntity.status(HttpStatus.CREATED).body(bookDto);
  }

  @PostMapping(value = "/isbn/ocr",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> extractIsbn(@RequestParam("image")MultipartFile image) {
    String barcode = bookService.extractIsbn(image);
    return ResponseEntity.ok(barcode);
  }

  @PatchMapping(value = "/{bookId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BookDto> update(
      @PathVariable UUID bookId,
      @RequestPart("bookData") BookUpdateRequest request,
      @RequestPart(value = "thumbnailImage", required = false) MultipartFile file) {

    BookDto bookDto = bookService.update(bookId, request, file);

    return ResponseEntity.ok(bookDto);
  }

  @GetMapping("/info")
  public ResponseEntity<NaverBookDto> info(String isbn) {
    NaverBookDto naverBookDto = apiService.searchBooks(isbn);
    return ResponseEntity.ok(naverBookDto);
  }

  @DeleteMapping("/{bookId}")
  public ResponseEntity<Void> deleteLogically(@PathVariable UUID bookId) {
    bookService.deleteLogically(bookId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{bookId}/hard")
  public ResponseEntity<Void> deletePhysically(@PathVariable UUID bookId) {
    bookService.deletePhysically(bookId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<CursorPageResponseBookDto> findAll(
      BookCondition condition) {
    CursorPageResponseBookDto result = bookSearchService.findAll(condition);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/popular")
  public ResponseEntity<CursorPageResponsePopularBookDto> findAll(
      PopularBookCondition condition) {
    CursorPageResponsePopularBookDto result = bookSearchService.findPopularBooks(condition);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/{bookId}")
  public ResponseEntity<BookDto> findById(@PathVariable UUID bookId) {
    BookDto bookDto = bookService.findById(bookId);
    return ResponseEntity.ok(bookDto);
  }
}
