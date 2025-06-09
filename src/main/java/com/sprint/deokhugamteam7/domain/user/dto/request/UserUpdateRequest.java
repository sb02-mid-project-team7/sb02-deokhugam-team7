package com.sprint.deokhugamteam7.domain.user.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @Pattern(
        regexp = "^\\S+$",
        message = "닉네임에는 공백을 포함할 수 없습니다."
    )
    @Size(min = 2, max = 20) String nickname
) {

}
