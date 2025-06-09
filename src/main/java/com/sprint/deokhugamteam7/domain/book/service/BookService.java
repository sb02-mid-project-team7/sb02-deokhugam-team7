package com.sprint.deokhugamteam7.domain.book.service;

import com.sprint.deokhugamteam7.domain.book.dto.BookDto;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookCreateRequest;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookUpdateRequest;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface BookService {

  BookDto create(BookCreateRequest request, MultipartFile image);

  String extractIsbn(MultipartFile image);

  BookDto update(UUID id, BookUpdateRequest request, MultipartFile image);

  BookDto findById(UUID id);

  void deleteLogically(UUID id);

  void deletePhysically(UUID id);

}
