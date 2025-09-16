package com.gyohwan.gyohwan.legacyYu.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplicantDetail {

    private Long id;
    private Integer rank;
    private Integer choice;
    private String nickname;
    private Double grade;
    private String lang;
}