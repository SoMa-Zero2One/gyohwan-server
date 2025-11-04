package com.gyohwan.gyohwan.window.dto;

import com.gyohwan.gyohwan.common.domain.Country;

import java.util.List;

public record CountryDetailResponse(
        String countryCode,
        String name,
        List<DataFieldDto> data,
        List<UnivSimpleDto> universities
) {

    public static CountryDetailResponse from(Country country, List<DataFieldDto> data, List<UnivSimpleDto> universities) {
        return new CountryDetailResponse(
                country.getCode(),
                country.getNameKo(),
                data,
                universities
        );
    }
}

