package com.gyohwan.gyohwan.window.dto;

import com.gyohwan.gyohwan.compare.domain.OutgoingUniv;

public record UnivSimpleDto(
        Long univId,
        String nameKo,
        String nameEn,
        String logoUrl
) {

    public static UnivSimpleDto from(OutgoingUniv univ) {
        return new UnivSimpleDto(
                univ.getId(),
                univ.getNameKo(),
                univ.getNameEn(),
                univ.getLogoUrl()
        );
    }
}

