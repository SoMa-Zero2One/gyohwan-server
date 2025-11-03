package com.gyohwan.gyohwan.community.dto;

import jakarta.validation.constraints.NotBlank;

public record PostUpdateRequest(
        @NotBlank(message = "제목은 비어 있을 수 없습니다.")
        String title,
        @NotBlank(message = "내용은 비어 있을 수 없습니다.")
        String content,
        String guestPassword
) {
}

