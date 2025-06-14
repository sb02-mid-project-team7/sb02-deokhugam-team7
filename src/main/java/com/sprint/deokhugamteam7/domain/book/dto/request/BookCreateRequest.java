package com.sprint.deokhugamteam7.domain.book.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "도서 정보")
public record BookCreateRequest(
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
    String isbn
) {
}
