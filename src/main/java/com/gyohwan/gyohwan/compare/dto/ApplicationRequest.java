package com.gyohwan.gyohwan.compare.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ApplicationRequest {

    private Double extraScore;

    @NotEmpty(message = "최소 하나 이상의 지망을 선택해야 합니다")
    @Valid
    private List<ChoiceRequest> choices;

    @NotNull(message = "학점 ID는 필수입니다")
    private Long gpaId;

    @NotNull(message = "어학 ID는 필수입니다")
    private Long languageId;

    @Getter
    @NoArgsConstructor
    public static class ChoiceRequest {

        @NotNull(message = "지망 순서는 필수입니다")
        private Integer choice;

        @NotNull(message = "슬롯 ID는 필수입니다")
        private Long slotId;
    }
}

