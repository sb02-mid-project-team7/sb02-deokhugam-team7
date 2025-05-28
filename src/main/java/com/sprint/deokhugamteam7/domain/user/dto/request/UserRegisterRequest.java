package com.sprint.deokhugamteam7.domain.user.dto.request;

public record UserRegisterRequest(
    String email,
    String nickname,
    String password
) {

}
