package com.gyohwan.gyohwan.window.dto;

import com.gyohwan.gyohwan.compare.domain.OutgoingUniv;

import java.util.List;

public record UnivDetailResponse(
        Long univId,
        String name,
        String logoUrl,
        String countryCode,
        String countryName,
        String continentCode,
        String continentName,
        List<DataFieldDto> data
) {

    public static UnivDetailResponse from(OutgoingUniv univ, List<DataFieldDto> data) {
        if (univ.getCountry() == null) {
            return new UnivDetailResponse(
                    univ.getId(),
                    univ.getNameKo(),
                    univ.getLogoUrl(),
                    null,
                    null,
                    null,
                    null,
                    data
            );
        }
        return new UnivDetailResponse(
                univ.getId(),
                univ.getNameKo(),
                univ.getLogoUrl(),
                univ.getCountry().getCode(),
                univ.getCountry().getNameKo(),
                univ.getCountry().getContinent() != null ? univ.getCountry().getContinent().getCode() : null,
                univ.getCountry().getContinent() != null ? univ.getCountry().getContinent().getNameKo() : null,
                data
        );
    }
}

