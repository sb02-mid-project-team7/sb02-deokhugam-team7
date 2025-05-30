package com.sprint.deokhugamteam7.domain.book.dto.response;

import java.util.List;

public record NaverBookResponse(
    String lastBuildDate,
    int total,
    int start,
    int display,
    List<Item> items
) {

}
