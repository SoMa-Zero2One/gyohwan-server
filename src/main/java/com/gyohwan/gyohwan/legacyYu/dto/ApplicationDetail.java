package com.gyohwan.gyohwan.legacyYu.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplicationDetail {
    private Integer choice; // 지망 순위
    private Long universityId;
    private String universityName;
    private String country;
    private Integer slot;
    private Integer totalApplicants;
}