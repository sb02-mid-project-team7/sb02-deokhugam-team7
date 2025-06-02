package com.sprint.deokhugamteam7.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    String email,

    @NotBlank(message = "비밀번호는 필수입니다")
    String password
) {

}
