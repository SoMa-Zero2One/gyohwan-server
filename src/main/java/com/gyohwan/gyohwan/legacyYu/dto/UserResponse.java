package com.gyohwan.gyohwan.legacyYu.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String nickname;
    private Double grade; // 학점
    private String lang; // 어학
    private Integer modifyCount;
    private List<ApplicationDetail> applications;
}