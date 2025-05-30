package com.sprint.deokhugamteam7.domain.book.controller;

import com.sprint.deokhugamteam7.domain.book.dto.BookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.BookDto;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookCreateRequest;
import com.sprint.deokhugamteam7.domain.book.dto.request.BookUpdateRequest;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponseBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.NaverBookDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
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

  @Operation(summary = "Book 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Book이 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = BookDto.class)))
  })
  ResponseEntity<BookDto> update(
      @Parameter(
          description = "도서 ID",
          required = true
      )
      UUID bookId,
      @Parameter(
          description = "수정할 도서 정보",
          required = true,
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      )
      BookUpdateRequest request,
      @Parameter(
          description = "수정할 도서 썸네일 이미지",
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
      )
      MultipartFile file
  );

  @Operation(summary = "Book 논리 삭제")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204",
          description = "Book이 성공적으로 삭제됨")
  })
  ResponseEntity<Void> deleteLogically(
      @Parameter(
          description = "도서 ID",
          required = true
      )
      UUID bookId
  );

  @Operation(summary = "Book 물리 삭제")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204",
          description = "Book이 성공적으로 삭제됨")
  })
  ResponseEntity<Void> deletePhysically(
      @Parameter(
          description = "도서 ID",
          required = true
      )
      UUID bookId
  );

  @Operation(summary = "ISBN으로 도서 정보 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Book정보가 성공적으로 조회됨")
  })
  ResponseEntity<NaverBookDto> info(
      @Parameter(
          description = "ISBN 번호",
          required = true
      )
    String isbn
  );

  @Operation(summary = "도서 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Book 목록이 성공적으로 조회됨")
  })
  ResponseEntity<CursorPageResponseBookDto> findAll(
      BookCondition condition
  );


  @Operation(summary = "도서 단건 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Book이 성공적으로 조회됨")
  })
  ResponseEntity<BookDto> findById(
      @Parameter(
          description = "도서 ID",
          required = true
      )
      UUID bookId
  );
}
