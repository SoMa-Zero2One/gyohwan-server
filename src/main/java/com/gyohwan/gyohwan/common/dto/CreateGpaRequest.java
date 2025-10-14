package com.gyohwan.gyohwan.common.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateGpaRequest(
        @NotNull @Positive
        Double score,

        @NotNull @Positive
        Double criteria
) {
}
