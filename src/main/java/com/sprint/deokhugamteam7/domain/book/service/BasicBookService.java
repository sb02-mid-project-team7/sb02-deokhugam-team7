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
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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
    if (!request.isbn().isBlank() && bookRepository.existsByIsbn(request.isbn())){
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
    Book book = bookRepository.findByIdAndIsDeletedFalse(id).orElseThrow(
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
  //TODO 출판일순, 평점순, 리뷰순 구현해야함
  @Override
  @Transactional(readOnly = true)
  public CursorPageResponseBookDto findAll(BookCondition condition) {
    Sort.Direction direction = Sort.Direction.fromString(condition.getDirection());
    Sort sort = Sort.by(direction, condition.getOrderBy());
    Pageable pageable = PageRequest.of(0, condition.getLimit(), sort);

    LocalDateTime cursor =Optional.ofNullable(condition.getCursor()).map(LocalDateTime::parse)
        .orElse(LocalDateTime.now());

    Slice<BookDto> bookSlice = bookRepository.findAllByKeyword(condition.getKeyword(),
        cursor, pageable)
        .map(BookDto::from);

    return CursorPageResponseBookDto.from(bookSlice);
  }

  @Override
  @Transactional(readOnly = true)
  public BookDto findById(UUID id) {
    Book book = bookRepository.findByIdAndIsDeletedFalse(id).orElseThrow(
        () -> new DeokhugamException(ErrorCode.INTERNAL_SERVER_ERROR)
    );
    return BookDto.from(book);
  }

  @Override
  @Transactional
  public void deleteLogically(UUID id) {
    Book book = bookRepository.findById(id).orElseThrow(
        () -> new DeokhugamException(ErrorCode.INTERNAL_SERVER_ERROR)
    );
    book.setIsDeleted(true);
    bookRepository.save(book);
    log.info("[BasicBookService] delete logically : id {}", book.getId());
  }

  @Override
  @Transactional
  public void deletePhysically(UUID id) {
    Book book = bookRepository.findById(id).orElseThrow(
        () -> new DeokhugamException(ErrorCode.INTERNAL_SERVER_ERROR)
    );
    log.info("[BasicBookService] delete Physically : id {}", book.getId());
    bookRepository.delete(book);
  }
}
