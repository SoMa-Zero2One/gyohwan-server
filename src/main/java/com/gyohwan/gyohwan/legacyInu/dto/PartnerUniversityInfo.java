package com.gyohwan.gyohwan.legacyInu.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartnerUniversityInfo {
    private Long id;
    private String nameKo;
    private String nameEn;
    private String country;
    private Integer slot;
    private Integer totalApplicants;
}
