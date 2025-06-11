package com.sprint.deokhugamteam7.domain.book.dto;

import com.sprint.deokhugamteam7.domain.book.dto.response.Item;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.Builder;

@Builder
@Schema(description = "네이버 API 응답 DTO")
public record NaverBookDto(
    @Schema(description = "도서 제목", example = "데미안")
    String title,
    @Schema(description = "작가", example = "헤르만 허세")
    String author,
    @Schema(description = "설명", example = "한 청년의 자아를 찾아가는 이야기")
    String description,
    @Schema(description = "출판사", example = "을유문화사")
    String publisher,
    @Schema(description = "출판일", example = "2023-05-10", type = "string", format = "date")
    LocalDate publishedDate,
    @Schema(description = "isbn", example = "9788990982575")
    String isbn,
    @Schema(
        description = "썸네일 이미지의 바이너리 데이터 (Base64 인코딩 예상)",
        type = "string",
        format = "byte"
    )
    byte[] thumbnailImage
) {

  public static NaverBookDto from(Item item) {
    return NaverBookDto.builder()
        .title(item.title())
        .author(item.author())
        .description(item.description())
        .publisher(item.publisher())
        .publishedDate(parseDate(item.pubdate()))
        .isbn(String.valueOf(item.isbn()))
        .thumbnailImage(downloadImage(item.image()))
        .build();
  }

  private static LocalDate parseDate(String date) {
    DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd");
    return LocalDate.parse(date, yyyyMMdd);
  }

  private static byte[] downloadImage(String imageUrl) {
    try (InputStream is = new URL(imageUrl).openStream()) {
      return is.readAllBytes();
    } catch (IOException e) {
      return null;
    }
  }
}
