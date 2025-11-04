package com.gyohwan.gyohwan.window.dto;

import com.gyohwan.gyohwan.compare.domain.OutgoingUniv;

import java.util.List;

public record UnivDetailResponse(
        Long univId,
        String name,
        String countryCode,
        String countryName,
        List<DataFieldDto> data
) {

    public static UnivDetailResponse from(OutgoingUniv univ, List<DataFieldDto> data) {
        return new UnivDetailResponse(
                univ.getId(),
                univ.getNameKo(),
                univ.getCountry() != null ? univ.getCountry().getCode() : null,
                univ.getCountry() != null ? univ.getCountry().getNameKo() : null,
                data
        );
    }
}

