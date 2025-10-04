package com.gyohwan.gyohwan.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailConfirmRequest(
        @Email @NotBlank(message = "이메일을 입력해 주세요.")
        String email,
        
        @NotBlank(message = "인증코드를 입력해 주세요.")
        String code
) {
}
