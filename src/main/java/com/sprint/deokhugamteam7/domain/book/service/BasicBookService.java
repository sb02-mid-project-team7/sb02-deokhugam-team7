package com.sprint.deokhugamteam7.domain.book.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.sprint.deokhugamteam7.domain.book.dto.BookDto;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookCreateRequest;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookUpdateRequest;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.repository.BookRepository;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import com.sprint.deokhugamteam7.exception.book.BookException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;
import javax.imageio.ImageIO;
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
  public BookDto create(BookCreateRequest request, MultipartFile image) {
    if (!request.isbn().isBlank() && bookRepository.existsByIsbn(request.isbn().trim())) {
      throw new BookException(ErrorCode.INTERNAL_BAD_REQUEST);
    }
//    log.info(
//        "[BasicBookService] create Request : title {}, author {}, description {}, publisher {}, publishedDate {}, isbn {}",
//        request.title(), request.author(), request.description(), request.publisher(),
//        request.publishedDate(), request.isbn());

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

//    log.info("[BasicBookService] create Book : id {}, title {}, created at {}, updated at {}",
//        book.getId(), book.getTitle(), book.getCreatedAt(), book.getUpdatedAt());

    return BookDto.from(book);
  }

  @Override
  public String extractIsbn(MultipartFile file) {
    try {
      BufferedImage image = ImageIO.read(file.getInputStream());
      if (image == null) {
        throw new BookException(ErrorCode.INTERNAL_BAD_REQUEST);
      }
      BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(
          image);
      BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
      Result result = new MultiFormatReader().decode(bitmap);
      if (result.getBarcodeFormat() != BarcodeFormat.EAN_13) {
        throw new BookException(ErrorCode.INTERNAL_BAD_REQUEST);
      }
      return result.getText();

    } catch (NotFoundException e) {
      throw new BookException(ErrorCode.INTERNAL_BAD_REQUEST);
    } catch (IOException e) {
      throw new BookException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  @Transactional
  public BookDto update(UUID id, BookUpdateRequest request, MultipartFile image) {
//    log.info(
//        "[BasicBookService] update Request : id {}, title {}, author {}, description {}, publisher {}, publishedDate {} ",
//        id, request.title(), request.author(), request.description(), request.publisher(),
//        request.publishedDate());
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
