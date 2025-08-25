package com.gyohwan.compass.legacyInu.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplicantDetail {
    private Long userId;
    private String nickname;
    private Integer choice;
    private Double grade;
    // 인천대는 어학 점수를 표시하지 않음 - lang 필드 제거
}
