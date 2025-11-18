package com.gyohwan.gyohwan.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * OutgoingUniv 정보 통합 수정 요청 DTO
 * - information 필드와 dataValues를 함께 수정할 수 있음
 */
@Getter
@NoArgsConstructor
public class UpdateOutgoingUnivRequest {
    
    private String information;
    private List<DataValueUpdateDto> dataValues;
}

