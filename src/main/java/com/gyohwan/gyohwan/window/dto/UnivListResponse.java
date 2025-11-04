package com.gyohwan.gyohwan.window.dto;

import com.gyohwan.gyohwan.compare.domain.OutgoingUniv;

import java.util.List;

public record UnivListResponse(
        Long univId,
        String name,
        String countryCode,
        String countryName,
        boolean isFavorite,
        List<DataFieldDto> data
) {

    public static UnivListResponse from(OutgoingUniv univ, boolean isFavorite, List<DataFieldDto> data) {
        return new UnivListResponse(
                univ.getId(),
                univ.getNameKo(),
                univ.getCountry() != null ? univ.getCountry().getCode() : null,
                univ.getCountry() != null ? univ.getCountry().getNameKo() : null,
                isFavorite,
                data
        );
    }
}

