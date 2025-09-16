package com.gyohwan.gyohwan.legacyInu.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PublicUserResponse {
    private Long id;
    private String nickname;
    private Double grade;
    // 인천대는 어학 정보를 표시하지 않음 - lang 필드 제거
    private List<ApplicationDetail> applications;
}
