package com.gyohwan.gyohwan.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record EmailSignupRequest(
        @NotBlank(message = "이메일을 입력해 주세요.")
        String email,

        @NotBlank(message = "비밀번호를 입력해 주세요.")
        String password
) {
}
