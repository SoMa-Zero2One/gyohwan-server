package com.gyohwan.gyohwan.window.dto;

import com.gyohwan.gyohwan.common.domain.Country;

import java.util.List;

public record CountryListResponse(
        String countryCode,
        String name,
        String continentCode,
        String continentName,
        List<DataFieldDto> data
) {

    public static CountryListResponse from(Country country, List<DataFieldDto> data) {
        return new CountryListResponse(
                country.getCode(),
                country.getNameKo(),
                country.getContinent() != null ? country.getContinent().getCode() : null,
                country.getContinent() != null ? country.getContinent().getNameKo() : null,
                data
        );
    }
}

