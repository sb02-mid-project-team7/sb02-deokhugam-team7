package com.sprint.deokhugamteam7.domain.book.controller;

import com.sprint.deokhugamteam7.domain.book.dto.BookDto;
import com.sprint.deokhugamteam7.domain.book.dto.NaverBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.condition.BookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.condition.PopularBookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookCreateRequest;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookUpdateRequest;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponseBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponsePopularBookDto;
import com.sprint.deokhugamteam7.swagger.BookDeleteSuccessResponse;
import com.sprint.deokhugamteam7.swagger.BookDuplicateResponse;
import com.sprint.deokhugamteam7.swagger.BookFindSuccessResponse;
import com.sprint.deokhugamteam7.swagger.BookIdParameter;
import com.sprint.deokhugamteam7.swagger.BookImageParameter;
import com.sprint.deokhugamteam7.swagger.BookNotFoundResponse;
import com.sprint.deokhugamteam7.swagger.BookRequestParameter;
import com.sprint.deokhugamteam7.swagger.InternalServerErrorResponse;
import com.sprint.deokhugamteam7.swagger.InvalidBadRequestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Book", description = "Book API")
public interface BookApi {

  @Operation(summary = "도서 목록 조회")
  @BookFindSuccessResponse
  @InvalidBadRequestResponse
  @InternalServerErrorResponse
  ResponseEntity<CursorPageResponseBookDto> findAll(
      @ParameterObject BookCondition condition
  );

  @Operation(summary = "Book 등록")
  @ApiResponse(
      responseCode = "201",
      description = "Book이 성공적으로 등록됨",
      content = @Content(schema = @Schema(implementation = BookDto.class)))
  @InvalidBadRequestResponse
  @BookDuplicateResponse
  @InternalServerErrorResponse
  ResponseEntity<BookDto> create(
      @BookRequestParameter BookCreateRequest request,
      @BookImageParameter MultipartFile file
  );

  @Operation(summary = "이미지 기반 ISBN 인식")
  @ApiResponse(
      responseCode = "200",
      description = "ISBN 인식 성공",
      content = @Content(
          mediaType = "text/plain",
          schema = @Schema(type = "string", example = "9788990982575")
      )
  )
  @InvalidBadRequestResponse
  @InternalServerErrorResponse
  ResponseEntity<String> extractIsbn(
      @BookImageParameter MultipartFile image);

  @Operation(summary = "도서 단건 조회")
  @BookFindSuccessResponse
  @BookNotFoundResponse
  @InternalServerErrorResponse
  ResponseEntity<BookDto> findById(
      @BookIdParameter UUID bookId
  );

  @Operation(summary = "Book 논리 삭제")
  @BookDeleteSuccessResponse
  @BookNotFoundResponse
  @InternalServerErrorResponse
  ResponseEntity<Void> deleteLogically(
      @BookIdParameter UUID bookId
  );

  @Operation(summary = "Book 수정")
  @ApiResponse(
      responseCode = "200",
      description = "Book이 성공적으로 수정됨",
      content = @Content(schema = @Schema(implementation = BookDto.class)))
  @InvalidBadRequestResponse
  @BookNotFoundResponse
  @BookDuplicateResponse
  @InternalServerErrorResponse
  ResponseEntity<BookDto>update(
      @BookIdParameter UUID bookId,
      @BookRequestParameter BookUpdateRequest request,
      @BookImageParameter MultipartFile file
  );
  @Operation(summary = "인기 도서 조회")
  @BookFindSuccessResponse
  @InvalidBadRequestResponse
  @InternalServerErrorResponse
  ResponseEntity<CursorPageResponsePopularBookDto> findAll(
      @ParameterObject PopularBookCondition condition);

  @Operation(summary = "ISBN으로 도서 정보 조회")
  @BookFindSuccessResponse
  @InvalidBadRequestResponse
  @BookNotFoundResponse
  @InternalServerErrorResponse
  ResponseEntity<NaverBookDto> info(
      @Parameter(
          description = "ISBN 번호",
          required = true
      )
      String isbn
  );

  @Operation(summary = "Book 물리 삭제")
  @BookDeleteSuccessResponse
  @BookNotFoundResponse
  @InternalServerErrorResponse
  ResponseEntity<Void> deletePhysically(
      @BookIdParameter UUID bookId
  );
}
