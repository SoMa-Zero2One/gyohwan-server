package com.gyohwan.gyohwan.common.dto;

import com.gyohwan.gyohwan.compare.domain.Language;

public record LanguageResponse(
        Long languageId,
        String testType,
        String score,
        String grade,
        String verifyStatus,
        String statusReason
) {
    public static LanguageResponse from(Language language) {
        return new LanguageResponse(
                language.getId(),
                language.getTestType().name(),
                language.getScore(),
                language.getGrade(),
                language.getVerifyStatus().name(),
                language.getStatusReason()
        );
    }
}
