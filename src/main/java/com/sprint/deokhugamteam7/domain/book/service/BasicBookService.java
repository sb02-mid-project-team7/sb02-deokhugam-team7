package com.sprint.deokhugamteam7.domain.book.service;

import com.sprint.deokhugamteam7.domain.book.dto.BookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.BookDto;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookCreateRequest;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookUpdateRequest;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponseBookDto;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.repository.BookRepository;
import com.sprint.deokhugamteam7.exception.DeokhugamException;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicBookService implements BookService{

  private final ImageService imageService;
  private final BookRepository bookRepository;

  @Override
  @Transactional
  public BookDto create(BookCreateRequest request, MultipartFile file) {
    if (bookRepository.existsByIsbn(request.isbn())) {
      throw new DeokhugamException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    String thumbnailUrl = null;
    if (file != null) {
      thumbnailUrl = imageService.uploadImage(file);
    }

    Book book = Book.create(request.title(), request.author(), request.publisher(),
            request.publishedDate())
        .description(request.description())
        .isbn(request.isbn())
        .thumbnailUrl(thumbnailUrl).build();
    bookRepository.save(book);

    log.info("[BasicBookService] create Book : id {}, title {}, created at {}, updated at {}",
        book.getId(), book.getTitle(), book.getCreatedAt(), book.getUpdatedAt());

    return BookDto.from(book);
  }

  @Override
  @Transactional
  public BookDto update(UUID id, BookUpdateRequest request, MultipartFile file) {
    Book book = bookRepository.findById(id).orElseThrow(
        () -> new DeokhugamException(ErrorCode.INTERNAL_SERVER_ERROR)
    );

    String thumbnailUrl = null;
    if (file != null) {
      thumbnailUrl = imageService.uploadImage(file);
    }

    book.update(request.title(), request.author(), request.description(), request.publisher(),
        request.publishedDate(), thumbnailUrl);
    bookRepository.save(book);

    log.info("[BasicBookService] update Book : id {}, title {}, created at {}, updated at {}",
        book.getId(), book.getTitle(), book.getCreatedAt(), book.getUpdatedAt());

    return BookDto.from(book);
  }

  @Override
  public CursorPageResponseBookDto findAll(BookCondition condition) {
    return null;
  }

  @Override
  public BookDto findById(UUID id) {
    return null;
  }

  @Override
  public void deleteLogically(UUID id) {

  }

  @Override
  public void deletePhysically(UUID uuid) {

  }
}
