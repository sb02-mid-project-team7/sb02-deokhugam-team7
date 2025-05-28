package com.sprint.deokhugamteam7.domain.book.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookCondition(
    //도서 제목
    String keyword,
    //페이지네이션 커서
    String cursor,
    //보조 커서
    LocalDateTime after,
    //정렬 기준
    String orderBy,
    //정렬 방향
    String direction,
    //페이지 크기
    int limit
) {

}
