package com.gyohwan.compass.legacyInu.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UniversityDetailResponse {
    private Long id;
    private String nameKo;
    private String nameEn;
    private String country;
    private Integer slot;
    private Integer totalApplicants;
    private List<ApplicantDetail> applicants;
}
