package com.gyohwan.gyohwan.window.dto;

import com.gyohwan.gyohwan.window.domain.DataValue;

public record DataFieldDto(
        Long fieldId,
        String fieldName,
        String value,
        String type
) {

    public static DataFieldDto from(DataValue dataValue) {
        return new DataFieldDto(
                dataValue.getField().getId(),
                dataValue.getField().getFieldName(),
                dataValue.getValue(),
                dataValue.getValueType().name()
        );
    }
}

