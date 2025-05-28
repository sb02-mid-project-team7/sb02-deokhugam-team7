package com.sprint.deokhugamteam7.domain.book.service;

import com.sprint.deokhugamteam7.domain.book.dto.BookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.BookDto;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookCreateRequest;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookUpdateRequest;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponseBookDto;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface BookService {

  //이미지를 등록할 경우 AWS S3에 이미지 올리기
  //해당 이미지 내용을 반환하기
  BookDto create(BookCreateRequest request, MultipartFile file);

  BookDto update(UUID id, BookUpdateRequest request, MultipartFile file);

  CursorPageResponseBookDto findAll(BookCondition condition);

  BookDto findById(UUID id);

  void deleteLogically(UUID id);

  void deletePhysically(UUID uuid);

}
