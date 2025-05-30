package com.sprint.deokhugamteam7.domain.book.dto.response;

public record Item(
    String title,
    String link,
    String image,
    String author,
    int discount,
    String publisher,
    long isbn,
    String description,
    String pubdate
    ) {

}
