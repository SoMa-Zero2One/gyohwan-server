package com.gyohwan.gyohwan.legacyInu.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplicationDetail {
    private Integer choice;
    private Long universityId;
    private String universityName;
    private String country;
    private Integer slot;
    private Integer totalApplicants;
}
