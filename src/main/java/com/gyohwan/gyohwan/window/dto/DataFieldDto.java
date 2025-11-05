package com.gyohwan.gyohwan.window.dto;

import com.gyohwan.gyohwan.window.domain.DataField;
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
                dataValue.getField().getValueType().name()
        );
    }

    public static DataFieldDto fromField(DataField field) {
        return new DataFieldDto(
                field.getId(),
                field.getFieldName(),
                null,
                field.getValueType().name()
        );
    }

    public static DataFieldDto fromFieldWithValue(DataField field, DataValue dataValue) {
        return new DataFieldDto(
                field.getId(),
                field.getFieldName(),
                dataValue != null ? dataValue.getValue() : null,
                field.getValueType().name()
        );
    }
}

