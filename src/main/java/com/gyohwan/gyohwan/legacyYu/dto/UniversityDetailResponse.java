package com.gyohwan.gyohwan.legacyYu.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UniversityDetailResponse {

    private String name;
    private String country;
    private Integer slot;
    private Integer totalApplicants;
    private List<ApplicantDetail> applicants;
}