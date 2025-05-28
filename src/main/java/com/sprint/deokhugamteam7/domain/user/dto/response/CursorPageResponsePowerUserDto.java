package com.sprint.deokhugamteam7.domain.user.dto.response;

import java.util.List;

public record CursorPageResponsePowerUserDto(
    List<PowerUserDto> content,
    String nextCursor,
    String nextAfter,
    int size,
    long totalElements,
    boolean hasNext
) {

}
