package com.gyohwan.gyohwan.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DataValue 업데이트용 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DataValueUpdateDto {
    
    private Long fieldId;
    private String value;
}

