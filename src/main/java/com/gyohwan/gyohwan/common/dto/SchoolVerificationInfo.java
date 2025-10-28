package com.gyohwan.gyohwan.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 학교 이메일 인증 정보를 Redis에 저장하기 위한 DTO
 */
@Getter
@NoArgsConstructor // Jackson 역직렬화를 위한 기본 생성자
@AllArgsConstructor // 서비스 로직에서 객체 생성을 위한 생성자
public class SchoolVerificationInfo {

    private String schoolEmail;
    private Long univId;
    private String code;

}
