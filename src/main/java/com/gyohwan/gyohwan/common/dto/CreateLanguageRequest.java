package com.gyohwan.gyohwan.common.dto;

import jakarta.validation.constraints.NotNull;

public record CreateLanguageRequest(
        @NotNull
        String testType,
        String score,
        String grade
) {
}
