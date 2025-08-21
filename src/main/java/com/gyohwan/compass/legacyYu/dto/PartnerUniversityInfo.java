package com.gyohwan.compass.legacyYu.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartnerUniversityInfo {
    private Long id;
    private String name;
    private String country;
    private Integer slot; // 총 모집인원
    private Integer applicantCount; // 현재 지원자 수
}