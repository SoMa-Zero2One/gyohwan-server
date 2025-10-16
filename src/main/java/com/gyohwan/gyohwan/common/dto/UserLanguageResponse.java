package com.gyohwan.gyohwan.common.dto;

import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.compare.domain.Language;

import java.util.List;

public record UserLanguageResponse(
        Long userId,
        List<LanguageInfo> languages
) {
    public record LanguageInfo(
            Long languageId,
            String testType,
            String score,
            String grade,
            String verifyStatus,
            String statusReason
    ) {
        public static LanguageInfo from(Language language) {
            return new LanguageInfo(
                    language.getId(),
                    language.getTestType().name(),
                    language.getScore(),
                    language.getGrade(),
                    language.getVerifyStatus().name(),
                    language.getStatusReason()
            );
        }
    }

    public static UserLanguageResponse from(User user) {
        return new UserLanguageResponse(
                user.getId(),
                user.getLanguages().stream()
                        .map(LanguageInfo::from)
                        .toList()
        );
    }
}
