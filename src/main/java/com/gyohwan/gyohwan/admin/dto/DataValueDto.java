package com.gyohwan.gyohwan.admin.dto;

import com.gyohwan.gyohwan.window.domain.DataValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DataValue 정보를 담는 DTO
 */
@Getter
@AllArgsConstructor
public class DataValueDto {
    
    private Long dataValueId;
    private Long fieldId;
    private String fieldName;
    private String value;

    public static DataValueDto from(DataValue dataValue) {
        return new DataValueDto(
                dataValue.getId(),
                dataValue.getField().getId(),
                dataValue.getField().getFieldName(),
                dataValue.getValue()
        );
    }
}

