package com.sprint.deokhugamteam7.domain.user.dto.request;

import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @Size(min = 2, max = 20) String nickname
) {

}
