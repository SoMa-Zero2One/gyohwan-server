package com.gyohwan.gyohwan.compare.dto;

import com.gyohwan.gyohwan.compare.domain.Season;

import java.time.LocalDateTime;
import java.util.List;

public record SeasonListResponse(
        SeasonDto[] seasons
) {

    public record SeasonDto(
            Long seasonId,
            String domesticUniversity,
            String domesticUniversityLogoUri,
            String name,
            LocalDateTime startDate,
            LocalDateTime endDate,
            boolean isApplied
    ) {
        public static SeasonDto from(Season season) {
            return new SeasonDto(
                    season.getId(),
                    season.getDomesticUniv().getName(),
                    "",
                    season.getName(),
                    season.getStartDate(),
                    season.getEndDate(),
                    false);
        }
    }

    public static SeasonListResponse from(List<Season> seasonList) {
        SeasonDto[] seasonDtoList = seasonList.stream()
                .map(SeasonDto::from)
                .toArray(SeasonDto[]::new);
        return new SeasonListResponse(seasonDtoList);
    }
}
