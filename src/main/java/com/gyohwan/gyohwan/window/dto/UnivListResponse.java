package com.gyohwan.gyohwan.window.dto;

import com.gyohwan.gyohwan.compare.domain.OutgoingUniv;

import java.util.List;

public record UnivListResponse(
        Long univId,
        String name,
        String countryCode,
        String countryName,
        String continentCode,
        String continentName,
        boolean isFavorite,
        List<DataFieldDto> data
) {

    public static UnivListResponse from(OutgoingUniv univ, boolean isFavorite, List<DataFieldDto> data) {
        if (univ.getCountry() == null) {
            return new UnivListResponse(
                    univ.getId(),
                    univ.getNameKo(),
                    null,
                    null,
                    null,
                    null,
                    isFavorite,
                    data
            );
        }
        return new UnivListResponse(
                univ.getId(),
                univ.getNameKo(),
                univ.getCountry().getCode(),
                univ.getCountry().getNameKo(),
                univ.getCountry().getContinent() == null ? null : univ.getCountry().getContinent().getCode(),
                univ.getCountry().getContinent() == null ? null : univ.getCountry().getContinent().getNameKo(),
                isFavorite,
                data
        );
    }
}

