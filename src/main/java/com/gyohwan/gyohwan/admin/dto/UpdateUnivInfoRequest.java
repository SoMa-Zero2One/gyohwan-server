package com.gyohwan.gyohwan.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * OutgoingUniv의 information 수정 요청 DTO
 */
@Getter
@NoArgsConstructor
public class UpdateUnivInfoRequest {
    
    private String information;
}

