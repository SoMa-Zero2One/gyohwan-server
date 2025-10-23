package com.gyohwan.gyohwan.compare.dto;

import java.time.LocalDateTime;

public record SeasonDetailResponse(
        Long seasonId,
        String domesticUniversity,
        String domesticUniversityLogoUri,
        String name,
        LocalDateTime startDate,
        LocalDateTime endDate,
        boolean hasApplied,
        long applicantCount
) {
}
