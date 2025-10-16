package com.gyohwan.gyohwan.common.dto;

import jakarta.validation.constraints.NotBlank;

public record SchoolEmailConfirmRequest(
        @NotBlank(message = "인증 코드는 필수입니다")
        String code
) {
}

