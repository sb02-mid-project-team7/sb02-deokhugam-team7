package com.sprint.deokhugamteam7.domain.book.service;

import com.sprint.deokhugamteam7.domain.book.dto.BookDto;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookCreateRequest;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookUpdateRequest;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.repository.BookRepository;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import com.sprint.deokhugamteam7.exception.book.BookException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BasicBookService implements BookService {

  private final ImageComponent imageComponent;
  private final BookRepository bookRepository;

  @Override
  @Transactional
  public BookDto create(BookCreateRequest request, MultipartFile image) {
    if (!request.isbn().isBlank() && bookRepository.existsByIsbn(request.isbn().trim())) {
      throw new BookException(ErrorCode.INTERNAL_BAD_REQUEST);
    }
    String thumbnailUrl = null;
    if (image != null) {
      thumbnailUrl = imageComponent.uploadImage(image);
    }
    Book book = Book.create(request.title(), request.author(), request.publisher(),
            request.publishedDate())
        .description(request.description())
        .isbn(request.isbn())
        .thumbnailUrl(thumbnailUrl).build();
    bookRepository.save(book);
    return BookDto.from(book);
  }

  @Override
  @Transactional
  public BookDto update(UUID id, BookUpdateRequest request, MultipartFile image) {
    Book book = bookRepository.findByIdAndIsDeletedFalse(id).orElseThrow(
        () -> new BookException(ErrorCode.BOOK_NOT_FOUND)
    );

    String thumbnailUrl = null;
    if (image != null) {
      thumbnailUrl = imageComponent.uploadImage(image);
    }

    book.update(request.title(), request.author(), request.description(), request.publisher(),
        request.publishedDate(), thumbnailUrl);
    bookRepository.save(book);
    return BookDto.from(book);
  }

  @Override
  @Transactional(readOnly = true)
  public BookDto findById(UUID id) {
    Book book = bookRepository.findByIdAndIsDeletedFalse(id).orElseThrow(
        () -> new BookException(ErrorCode.BOOK_NOT_FOUND)
    );
    return BookDto.from(book);
  }

  @Override
  @Transactional
  public void deleteLogically(UUID id) {
    Book book = bookRepository.findById(id).orElseThrow(
        () -> new BookException(ErrorCode.BOOK_NOT_FOUND)
    );
    book.setIsDeleted(true);
    bookRepository.save(book);
  }

  @Override
  @Transactional
  public void deletePhysically(UUID id) {
    Book book = bookRepository.findById(id).orElseThrow(
        () -> new BookException(ErrorCode.BOOK_NOT_FOUND)
    );
    bookRepository.delete(book);
  }
}
