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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicBookService implements BookService {

  private final ImageComponent imageComponent;
  private final BookRepository bookRepository;

  @Override
  @Transactional
  public BookDto create(BookCreateRequest request, MultipartFile file) {
    if (!request.isbn().isBlank() && bookRepository.existsByIsbn(request.isbn().trim())) {
      throw new BookException(ErrorCode.INTERNAL_BAD_REQUEST);
    }
//    log.info(
//        "[BasicBookService] create Request : title {}, author {}, description {}, publisher {}, publishedDate {}, isbn {}",
//        request.title(), request.author(), request.description(), request.publisher(),
//        request.publishedDate(), request.isbn());

    String thumbnailUrl = null;
    if (file != null) {
      thumbnailUrl = imageComponent.uploadImage(file);
    }

    Book book = Book.create(request.title(), request.author(), request.publisher(),
            request.publishedDate())
        .description(request.description())
        .isbn(request.isbn())
        .thumbnailUrl(thumbnailUrl).build();
    bookRepository.save(book);

//    log.info("[BasicBookService] create Book : id {}, title {}, created at {}, updated at {}",
//        book.getId(), book.getTitle(), book.getCreatedAt(), book.getUpdatedAt());

    return BookDto.from(book);
  }

  @Override
  @Transactional
  public BookDto update(UUID id, BookUpdateRequest request, MultipartFile file) {
//    log.info(
//        "[BasicBookService] update Request : id {}, title {}, author {}, description {}, publisher {}, publishedDate {} ",
//        id, request.title(), request.author(), request.description(), request.publisher(),
//        request.publishedDate());
    Book book = bookRepository.findByIdAndIsDeletedFalse(id).orElseThrow(
        () -> new BookException(ErrorCode.BOOK_NOT_FOUND)
    );

    String thumbnailUrl = null;
    if (file != null) {
      thumbnailUrl = imageComponent.uploadImage(file);
    }

    book.update(request.title(), request.author(), request.description(), request.publisher(),
        request.publishedDate(), thumbnailUrl);
    bookRepository.save(book);

//    log.info("[BasicBookService] update Book : id {}, title {}, created at {}, updated at {}",
//        book.getId(), book.getTitle(), book.getCreatedAt(), book.getUpdatedAt());

    return BookDto.from(book);
  }

  @Override
  @Transactional(readOnly = true)
  public BookDto findById(UUID id) {
//    log.info("[BasicBookService] find Request : id {}", id);
    Book book = bookRepository.findByIdAndIsDeletedFalse(id).orElseThrow(
        () -> new BookException(ErrorCode.BOOK_NOT_FOUND)
    );
    return BookDto.from(book);
  }

  @Override
  @Transactional
  public void deleteLogically(UUID id) {
//    log.info("[BasicBookService] delete Logically Request : id {}", id);
    Book book = bookRepository.findById(id).orElseThrow(
        () -> new BookException(ErrorCode.BOOK_NOT_FOUND)
    );
    book.setIsDeleted(true);
    bookRepository.save(book);
//    log.info("[BasicBookService] delete logically Successfully: id {}", book.getId());
  }

  @Override
  @Transactional
  public void deletePhysically(UUID id) {
//    log.info("[BasicBookService] delete Physically Request : id {}", id);
    Book book = bookRepository.findById(id).orElseThrow(
        () -> new BookException(ErrorCode.BOOK_NOT_FOUND)
    );
//    log.info("[BasicBookService] delete Physically Successfully: id {}", book.getId());
    bookRepository.delete(book);
  }
}
