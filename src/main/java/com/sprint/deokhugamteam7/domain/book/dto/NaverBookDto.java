package com.sprint.deokhugamteam7.domain.book.dto;

import com.sprint.deokhugamteam7.domain.book.dto.response.Item;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.Builder;

@Builder
public record NaverBookDto(
    String title,
    String author,
    String description,
    String publisher,
    LocalDate publishedDate,
    String isbn,
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
