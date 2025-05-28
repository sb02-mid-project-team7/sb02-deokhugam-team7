package com.sprint.deokhugamteam7.domain.book.controller;

import com.sprint.deokhugamteam7.domain.book.dto.BookDto;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Book", description = "Book API")
public interface BookApi {

  @Operation(summary = "Book 등록")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201",
          description = "Book이 성공적으로 등록됨",
          content = @Content(schema = @Schema(implementation = BookDto.class)))
  })
  ResponseEntity<BookDto> create(
      @Parameter(
          description = "도서 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      )
      BookCreateRequest request,
      @Parameter(
          description = "도서 썸네일 이미지",
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
      )
      MultipartFile file
  );
}
