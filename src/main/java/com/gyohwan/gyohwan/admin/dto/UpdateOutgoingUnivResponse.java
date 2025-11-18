package com.gyohwan.gyohwan.admin.dto;

import com.gyohwan.gyohwan.compare.domain.OutgoingUniv;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * OutgoingUniv 정보 통합 수정 응답 DTO
 */
@Getter
@AllArgsConstructor
public class UpdateOutgoingUnivResponse {
    
    private Long univId;
    private String nameEn;
    private String nameKo;
    private String information;
    private List<DataValueDto> dataValues;

    public static UpdateOutgoingUnivResponse of(OutgoingUniv outgoingUniv, List<DataValueDto> dataValues) {
        return new UpdateOutgoingUnivResponse(
                outgoingUniv.getId(),
                outgoingUniv.getNameEn(),
                outgoingUniv.getNameKo(),
                outgoingUniv.getInformation(),
                dataValues
        );
    }
}

