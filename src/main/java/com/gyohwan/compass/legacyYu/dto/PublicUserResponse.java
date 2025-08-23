package com.gyohwan.compass.legacyYu.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PublicUserResponse {
    private Long id;
    private String nickname;
    private Double grade;
    private String lang;
    private List<ApplicationDetail> applications;
}

